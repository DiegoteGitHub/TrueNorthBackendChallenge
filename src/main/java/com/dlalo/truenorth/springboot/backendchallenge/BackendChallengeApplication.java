package com.dlalo.truenorth.springboot.backendchallenge;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.dlalo.truenorth.springboot.backendchallenge.model.Campsite;
import com.dlalo.truenorth.springboot.backendchallenge.repository.CampsiteRepository;

@SpringBootApplication(scanBasePackages = {"com.dlalo.truenorth.springboot.backendchallenge"})
public class BackendChallengeApplication {

	public static void main(String[] args) {
		System.setProperty("spring.devtools.restart.enabled", "true");
		SpringApplication.run(BackendChallengeApplication.class, args);
	}
	
	@Bean	
	public CommandLineRunner demo(CampsiteRepository campsiteRepository) {
		return (args) -> {
			// Create the campsite
			Campsite campsite = new Campsite();
			campsite.setName("Campsite one");
			campsiteRepository.save(campsite);
		};
	}
}
