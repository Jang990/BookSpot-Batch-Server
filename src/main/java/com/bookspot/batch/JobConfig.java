package com.bookspot.batch;

import com.bookspot.batch.book.BookStepConst;
import com.bookspot.batch.library.LibraryStepConst;
import com.bookspot.batch.stock.StockStepConst;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobConfig {
    private final JobRepository jobRepository;

    private final BookSpotSteps bookSpotSteps;

    @Bean
    public Job bookSpot() {
        return new JobBuilder("bookSpot", jobRepository)
//                .start(getStep(LibraryStepConst.STEP_NAME))

                .start(getStep(BookStepConst.STEP_NAME))
                .next(getStep(StockStepConst.STEP_NAME))

                .incrementer(new RunIdIncrementer())
                .build();
    }

    private Step getStep(String stepName) {
        return bookSpotSteps.getStep(stepName);
    }
}
