package com.e_rental.owner;

import com.e_rental.owner.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class OwnerApplication {
//	@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
	public static void main(String[] args) {
		SpringApplication.run(OwnerApplication.class, args);
	}

}
