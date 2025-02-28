package com.bookspot.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * - DB에 있는 ISBN 정보를 메모리로 불러오기
 * - 도서관 재고 정보 (1500개) 파일 읽기
 *   - 도서 정보(isbn13, 도서명, 저자 등등) 파싱
 *   - 메모리에 존재하는 ISBN 필터링
 *   - INSERT IGNORE + ISBN 메모리 업데이트
 * - 도서 정보 인메모리 clearAll();
 */
@Configuration
@RequiredArgsConstructor
public class BookSyncJobConfig {
    private final JobRepository jobRepository;

    @Bean
    public Job isbnWarmUpJob(Step inMemoryIsbnWarmUpStep) {
        return new JobBuilder("inMemoryIsbnWarmUpJob", jobRepository)
                .start(inMemoryIsbnWarmUpStep)
                .build();
    }

    @Bean
    public Job bookSyncJob(Step bookSyncStep) {
        return new JobBuilder("bookSyncJob", jobRepository)
                .start(bookSyncStep)
                .build();
    }

    @Bean
    public Job isbnMemoryClearJob(Step inMemoryIsbnClearStep) {
        return new JobBuilder("inMemoryIsbnClearJob", jobRepository)
                .start(inMemoryIsbnClearStep)
                .build();
    }
}
