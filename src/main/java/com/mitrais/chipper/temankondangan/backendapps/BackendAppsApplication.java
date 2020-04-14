package com.mitrais.chipper.temankondangan.backendapps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.mitrais.chipper.temankondangan.backendapps.config.AppProperties;

@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties(AppProperties.class)
public class BackendAppsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendAppsApplication.class, args);
	}
}
