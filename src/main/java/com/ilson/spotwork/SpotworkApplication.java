package com.ilson.spotwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpotworkApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpotworkApplication.class, args);
	}

}
