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
public class JobConfig {
    private final JobRepository jobRepository;


    @Bean
    public Job librarySyncJob(
            Step libraryExcelDownloadStep,
            Step librarySyncStep,
            Step libraryExcelDeleteStep,
            Step libraryNaruDetailParsingStep,
            Step stockCsvDownloadStep) {
        return new JobBuilder("librarySyncJob", jobRepository)

                // 도서관 파일 다운로드 -> 도서관 파일 정보 저장 -> 파일 삭제 -> naru_detail 파싱
                .start(libraryExcelDownloadStep)
                .next(librarySyncStep)
                .next(libraryExcelDeleteStep)
                .next(libraryNaruDetailParsingStep)

                // naru_detail이 있는 도서관 파일 다운로드
                .next(stockCsvDownloadStep)
                .build();
    }

    @Bean
    public Job stockSyncJob(
            Step stockSyncStep,
            Step missingStockDeleteStep,
            Step stockCsvDeleteStep,
            Step stockUpdatedAtStep) {
        // 재고 업데이트 -> 사라진 재고 정보 삭제 -> 파일 삭제 -> 크롤링 시점 업데이트
        return new JobBuilder("stockFileJob", jobRepository)
                .start(stockSyncStep)
                .next(missingStockDeleteStep)
                .next(stockCsvDeleteStep)
                .next(stockUpdatedAtStep)
                .build();
    }

    @Bean
    public Job isbnWarmUpJob(Step isbnWarmUpStep) {
        return new JobBuilder("isbnWarmUpJob", jobRepository)
                .start(isbnWarmUpStep)
                .build();
    }

    @Bean
    public Job isbnMemoryClearJob(Step isbnMemoryClearStep) {
        return new JobBuilder("isbnMemoryClearJob", jobRepository)
                .start(isbnMemoryClearStep)
                .build();
    }

}
