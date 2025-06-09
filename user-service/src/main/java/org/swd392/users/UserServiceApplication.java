package org.swd392.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UserServiceApplication {

	public static void main(String[] args) {
		System.out.println("Starting User Service Application...");
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
