package com.mitrais.chipper.temankondangan.backendapps;

import com.mitrais.chipper.temankondangan.backendapps.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties(AppProperties.class)
public class BackendAppsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendAppsApplication.class, args);
	}
}
