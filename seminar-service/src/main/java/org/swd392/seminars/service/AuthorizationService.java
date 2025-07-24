package org.swd392.seminars.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.swd392.seminars.dto.ApiResponse;
import org.swd392.seminars.dto.UserInfoDto;
import org.swd392.seminars.enums.UserRole;
import org.swd392.seminars.exception.UnauthorizedException;
import org.swd392.seminars.service.client.UserFeignClient;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {
    
    private final UserFeignClient userFeignClient;
    
    /**
     * Validate if user has required role
     */
    public void validateUserRole(Integer userId, UserRole[] requiredRoles) {
        if (userId == null) {
            throw new UnauthorizedException("User ID is required");
        }
        
        try {
            var userResponse = userFeignClient.getUserDetails(Long.valueOf(userId));
            
            if (userResponse == null || userResponse.getBody() == null) {
                throw new UnauthorizedException("Unable to fetch user information");
            }
            
            ApiResponse<UserInfoDto> apiResponse = userResponse.getBody();
            UserInfoDto userInfo = apiResponse.getResult();
            
            if (userInfo == null || userInfo.getRole() == null) {
                throw new UnauthorizedException("User role information not found");
            }
            
            String userRole = userInfo.getRole();
            boolean hasRequiredRole = Arrays.stream(requiredRoles)
                    .anyMatch(role -> role.name().equals(userRole));
            
            if (!hasRequiredRole) {
                log.warn("User {} with role {} attempted to access endpoint requiring roles: {}", 
                        userId, userRole, Arrays.toString(requiredRoles));
                throw new UnauthorizedException("Insufficient permissions. Required roles: " + 
                        Arrays.toString(requiredRoles) + ", but user has role: " + userRole);
            }
            
            log.debug("User {} with role {} authorized successfully", userId, userRole);
            
        } catch (Exception e) {
            if (e instanceof UnauthorizedException) {
                throw e;
            }
            log.error("Error validating user role for user {}: {}", userId, e.getMessage());
            throw new UnauthorizedException("Authorization validation failed: " + e.getMessage());
        }
    }
    
    /**
     * Get user information by ID
     */
    public UserInfoDto getUserInfo(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        try {
            var userResponse = userFeignClient.getUserDetails(Long.valueOf(userId));
            
            if (userResponse == null || userResponse.getBody() == null) {
                throw new RuntimeException("Unable to fetch user information");
            }
            
            ApiResponse<UserInfoDto> apiResponse = userResponse.getBody();
            return apiResponse.getResult();
            
        } catch (Exception e) {
            log.error("Error fetching user info for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to fetch user information: " + e.getMessage());
        }
    }
    
    /**
     * Check if user has specific role
     */
    public boolean hasRole(Integer userId, UserRole role) {
        try {
            UserInfoDto userInfo = getUserInfo(userId);
            return userInfo != null && role.name().equals(userInfo.getRole());
        } catch (Exception e) {
            log.error("Error checking role for user {}: {}", userId, e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if user has any of the specified roles
     */
    public boolean hasAnyRole(Integer userId, UserRole... roles) {
        try {
            UserInfoDto userInfo = getUserInfo(userId);
            if (userInfo == null || userInfo.getRole() == null) {
                return false;
            }
            
            return Arrays.stream(roles)
                    .anyMatch(role -> role.name().equals(userInfo.getRole()));
        } catch (Exception e) {
            log.error("Error checking roles for user {}: {}", userId, e.getMessage());
            return false;
        }
    }
}
