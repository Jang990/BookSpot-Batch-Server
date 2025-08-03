package com.bookspot.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class BookspotBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookspotBatchApplication.class, args);
	}

}
