package com.bookspot.batch.job;

import com.bookspot.batch.step.service.memory.book.InMemoryJdkBookService;
import com.bookspot.batch.step.writer.file.book.AggregatedBooksCsvWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BookSyncJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final InMemoryJdkBookService inMemoryBookService;
    private final AggregatedBooksCsvWriter aggregatedBooksCsvWriter;

    @Bean
    public Job aggregateBookFileJob(Step libraryBookSyncStep, Step syncAggregatedBookStep) {
        return new JobBuilder("aggregateBookFileJob", jobRepository)
                .start(libraryBookSyncStep) // 도서관 재고 정보 파일 읽기
                .next(aggregateBookFileStep())// 인메모리에 저장한 정보를 파일로 저장
                .next(clearBookMemoryStep()) // 도서 정보 인메모리 clearAll();
                .next(syncAggregatedBookStep) //- 저장한 파일을 DB에 반영 - 새로 나온 책 + 최근 대출 횟수 반영
                .build();
    }

    @Bean
    public Step clearBookMemoryStep() {
        return new StepBuilder("clearBookMemoryStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    inMemoryBookService.clearAll();
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step aggregateBookFileStep() {
        return new StepBuilder("aggregateBookFileStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    aggregatedBooksCsvWriter.saveToCsv(inMemoryBookService.getData());
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
