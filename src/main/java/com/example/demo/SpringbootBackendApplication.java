package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class SpringbootBackendApplication {

	@Autowired
	public static void main(String[] args) {
		SpringApplication.run(SpringbootBackendApplication.class, args);	
	}
	
}
