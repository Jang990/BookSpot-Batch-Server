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
public class BookSyncJobConfig {
    private final JobRepository jobRepository;

    @Bean
    public Job aggregateBookFileJob(Step libraryBookSyncStep) {
        return new JobBuilder("aggregateBookFileJob", jobRepository)
                .start(libraryBookSyncStep) // - 도서관 재고 정보 파일 읽기
                //- 인메모리에 저장한 정보를 파일로 저장
                //- 도서 정보 인메모리 clearAll();
                //- 저장한 파일을 DB에 반영 - 새로 나온 책 + 최근 대출 횟수 반영
                .build();
    }
}
