package com.mitrais.chipper.temankondangan.backendapps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@EnableAutoConfiguration
@Configuration
public class BackendAppsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendAppsApplication.class, args);
	}
}
