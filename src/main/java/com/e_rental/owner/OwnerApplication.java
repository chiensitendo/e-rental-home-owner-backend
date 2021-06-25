package com.e_rental.owner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class OwnerApplication {
//	@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
	public static void main(String[] args) {
		SpringApplication.run(OwnerApplication.class, args);
	}

}
