package com.example.FreightGrid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FreightGridApplication {

	public static void main(String[] args) {
		SpringApplication.run(FreightGridApplication.class, args);
	}

}
