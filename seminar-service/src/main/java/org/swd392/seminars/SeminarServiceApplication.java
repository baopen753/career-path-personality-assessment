package org.swd392.seminars;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@OpenAPIDefinition(info = @Info(title = "Seminar Service API", version = "1.0", description = "API for Seminar Service"))
public class SeminarServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeminarServiceApplication.class, args);
    }

}
