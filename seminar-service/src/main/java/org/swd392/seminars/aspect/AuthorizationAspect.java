package org.swd392.seminars.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.swd392.seminars.annotation.RequireRole;
import org.swd392.seminars.service.AuthorizationService;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationAspect {

    private final AuthorizationService authorizationService;

    @Before("@annotation(requireRole)")
    public void checkAuthorization(JoinPoint joinPoint, RequireRole requireRole) {
        log.debug("Checking authorization for method: {}", joinPoint.getSignature().getName());
        
        // Get current HTTP request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        
        // Extract X-User-Id from request header
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader == null || userIdHeader.trim().isEmpty()) {
            throw new org.swd392.seminars.exception.UnauthorizedException("X-User-Id header is required");
        }
        
        Integer userId;
        try {
            userId = Integer.valueOf(userIdHeader);
        } catch (NumberFormatException e) {
            throw new org.swd392.seminars.exception.UnauthorizedException("Invalid X-User-Id format");
        }
        
        // Validate user role
        authorizationService.validateUserRole(userId, requireRole.value());
        
        log.debug("Authorization successful for user {} with required roles: {}", 
                userId, java.util.Arrays.toString(requireRole.value()));
    }
}
