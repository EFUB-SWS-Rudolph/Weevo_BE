package com.rudolph.Weevo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WeevoApplication {
	public static void main(String[] args) {
		SpringApplication.run(WeevoApplication.class, args);
	}
}

