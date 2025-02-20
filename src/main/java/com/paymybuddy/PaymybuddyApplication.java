package com.paymybuddy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication // Indicates a Spring Boot application
public class PaymybuddyApplication {

	private static final Logger logger = LoggerFactory.getLogger(PaymybuddyApplication.class);

	/**
	 * Main method to run the Spring Boot application.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		logger.info("Starting PaymybuddyApplication...");
		ConfigurableApplicationContext context = SpringApplication.run(PaymybuddyApplication.class, args);
		logger.info("PaymybuddyApplication started successfully.");
	}

}
