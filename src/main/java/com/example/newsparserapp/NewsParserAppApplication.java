package com.example.newsparserapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NewsParserAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsParserAppApplication.class, args);
	}

}
