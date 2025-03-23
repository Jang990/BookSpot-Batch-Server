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
public class StockSyncJobConfig {
    private final JobRepository jobRepository;

    @Bean
    public Job stockSyncJob(
            Step stockSyncPartitionMasterStep,
            Step missingStockDeleteStep,
            Step stockCsvDeleteStep,
            Step stockUpdatedAtStep) {
        // 재고 업데이트 -> 사라진 재고 정보 삭제 -> 파일 삭제 -> 크롤링 시점 업데이트
        return new JobBuilder("stockFileJob", jobRepository)
                .start(stockSyncPartitionMasterStep)
                .next(missingStockDeleteStep)
                .next(stockCsvDeleteStep)
                .next(stockUpdatedAtStep)
                .build();
    }
}
