package com.bookspot.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StockFileJob {
    private final JobRepository jobRepository;

    @Bean
    public Job stockFileJob(Step stockCsvDownloadStep) {
        // naru_detail이 있는 도서관 파일 다운로드
        return new JobBuilder("stockFileJob", jobRepository)
                .start(stockCsvDownloadStep)
                .build();
    }
}
