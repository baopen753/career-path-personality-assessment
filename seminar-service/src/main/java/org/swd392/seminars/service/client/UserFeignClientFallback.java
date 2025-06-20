package org.swd392.seminars.service.client;

import org.springframework.stereotype.Component;

@Component
public class UserFeignClientFallback implements UserFeignClient {

    @Override
    public String getUserEmail(Integer userId) {
        System.err.println("UserFeignClient fallback: getUserEmail for userId " + userId);
        return "fallback@example.com";
    }

    @Override
    public String getUserName(Integer userId) {
        System.err.println("UserFeignClient fallback: getUserName for userId " + userId);
        return "Fallback User";
    }

    @Override
    public String getUserRole(Integer userId) {
        System.err.println("UserFeignClient fallback: getUserRole for userId " + userId);
        return "STUDENT"; // Default fallback role
    }

    @Override
    public Object getUserInfo(Integer userId) {
        System.err.println("UserFeignClient fallback: getUserInfo for userId " + userId);
        return null;
    }
} 