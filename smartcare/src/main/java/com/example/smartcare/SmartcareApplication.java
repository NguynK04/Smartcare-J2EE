package com.example.smartcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SmartcareApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartcareApplication.class, args);
	}

}
