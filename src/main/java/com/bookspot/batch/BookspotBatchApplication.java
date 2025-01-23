package com.bookspot.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookspotBatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BookspotBatchApplication.class, args);
	}


	// 임시 코드
	@Autowired JobLauncher jobLauncher;
	@Autowired private Job tempJob;

	@Override
	public void run(String... args) throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("filePath", "bookSpotFiles/stock/1.csv")
				.toJobParameters();

		jobLauncher.run(tempJob, jobParameters);
	}
}
