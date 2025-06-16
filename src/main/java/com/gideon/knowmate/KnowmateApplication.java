package com.gideon.knowmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class KnowmateApplication {

	public static void main(String[] args) {
		SpringApplication.run(KnowmateApplication.class, args);
	}

}
