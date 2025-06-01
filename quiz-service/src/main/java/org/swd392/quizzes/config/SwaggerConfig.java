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
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8802");
        devServer.setDescription("Development Server");

        Server prodServer = new Server();
        prodServer.setUrl("https://api.personalityquiz.com");
        prodServer.setDescription("Production Server");

        Contact contact = new Contact();
        contact.setEmail("admin@personalityquiz.com");
        contact.setName("Quiz Service Team");

        License license = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Quiz Service API")
                .version("1.0")
                .contact(contact)
                .description("API for managing personality quizzes, questions, and results")
                .license(license);

        return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
    }
}
