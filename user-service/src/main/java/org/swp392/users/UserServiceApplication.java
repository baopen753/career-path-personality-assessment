package org.swp392.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		System.out.println("Starting User Service Application...");
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
