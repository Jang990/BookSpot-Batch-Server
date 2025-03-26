package com.bookspot.batch.job;

import com.bookspot.batch.job.listener.StockSyncJobListener;
import com.bookspot.batch.job.validator.FilePathJobParameterValidator;
import com.bookspot.batch.step.reader.IsbnIdReader;
import com.bookspot.batch.step.service.memory.bookid.IsbnMemoryRepository;
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

    private final IsbnIdReader isbnIdReader;
    private final IsbnMemoryRepository isbnEclipseMemoryRepository;

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
                .listener(new StockSyncJobListener(isbnIdReader, isbnEclipseMemoryRepository))
                .validator(FilePathJobParameterValidator.onlyRootDir())
                .build();
    }
}
