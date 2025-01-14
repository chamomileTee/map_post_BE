package com.example.pinboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * PinboardApplication
 * <p>Pinboard Application</p>
 * @since 2025-01-09
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 * @see SpringBootApplication
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class PinboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(PinboardApplication.class, args);
	}

}
