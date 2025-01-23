package com.bookspot.batch.job;

import com.bookspot.batch.step.StockStepConst;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TempJobConfig {
    private final JobRepository jobRepository;
    private final BookSpotSteps bookSpotSteps;

    @Bean
    public Job tempJob() {
        return new JobBuilder("tempJob(", jobRepository)
                .start(bookSpotSteps.getStep("tempStep"))
                .incrementer(new RunIdIncrementer())
                .build();
    }
}
