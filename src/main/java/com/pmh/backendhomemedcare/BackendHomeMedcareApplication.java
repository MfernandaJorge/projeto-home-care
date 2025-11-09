package com.pmh.backendhomemedcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BackendHomeMedcareApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendHomeMedcareApplication.class, args);
	}
}
