package com.example.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class AggregatorApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(AggregatorApplication.class);
		app.setAdditionalProfiles("aggregator");
		app.run(args);
	}

}
