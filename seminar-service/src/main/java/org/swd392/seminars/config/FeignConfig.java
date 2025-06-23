package org.swd392.seminars.config;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Retryer feignRetryer() {
        // Retry 3 times with exponential backoff: 1s, 2s, 4s
        return new Retryer.Default(1000, 4000, 3);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    public static class CustomErrorDecoder implements ErrorDecoder {
        @Override
        public Exception decode(String methodKey, feign.Response response) {
            // Log the error for debugging
            System.err.println("Feign error - Method: " + methodKey + ", Status: " + response.status());
            
            // Return a more descriptive exception
            return new RuntimeException("Service call failed: " + methodKey + " - Status: " + response.status());
        }
    }
} 