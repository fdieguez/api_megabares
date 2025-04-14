package com.megabares.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@SpringBootApplication
@EnableScheduling
public class ApiMegabaresApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApiMegabaresApplication.class, args);
	}
}
