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
            Step libraryExcelDeleteStep,
            Step libraryInsertStep,
            Step libraryNaruDetailParsingStep,
            Step stockCsvDownloadStep) {
        return new JobBuilder("librarySyncJob", jobRepository)

                // 도서관 파일 다운로드 -> 도서관 파일 정보 저장 -> 파일 삭제 -> naru_detail 파싱
                .start(libraryExcelDownloadStep)
                .next(libraryInsertStep)
                .next(libraryExcelDeleteStep)
                .next(libraryNaruDetailParsingStep)

                // naru_detail이 있는 도서관 파일 다운로드
                .start(stockCsvDownloadStep)
                .build();
    }

    @Bean
    public Job stockFileJob(
            Step bookUpdateStep,
            Step libraryStockUpdateStep,
            Step stockCsvDeleteStep,
            Step libraryStockUpdatedAtStep) {
        // 책 업데이트 -> 재고 업데이트 -> 파일 삭제 -> 크롤링 시점 업데이트
        return new JobBuilder("stockFileJob", jobRepository)
                .start(bookUpdateStep)
                .next(libraryStockUpdateStep)
                .next(stockCsvDeleteStep)
                .next(libraryStockUpdatedAtStep)
                .build();
    }

}
