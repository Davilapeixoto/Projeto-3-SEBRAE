package com.projeto3.SEBRAE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class SebraeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SebraeApplication.class, args);
	}

}
