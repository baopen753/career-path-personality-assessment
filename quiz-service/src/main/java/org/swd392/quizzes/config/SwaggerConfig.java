package org.swd392.quizzes.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI quizServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Quiz Service API")
                        .description("Quiz Service APIs for SBA301")
                        .version("1.0"));
    }
}
