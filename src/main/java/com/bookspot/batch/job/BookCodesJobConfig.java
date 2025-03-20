package com.bookspot.batch.job;

import com.bookspot.batch.data.mapper.BookCodesMapper;
import com.bookspot.batch.global.crawler.kdc.KdcCode;
import com.bookspot.batch.global.crawler.kdc.KdcCrawler;
import com.bookspot.batch.step.service.BookCodesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BookCodesJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final KdcCrawler kdcCrawler;
    private final BookCodesMapper bookCodesMapper;
    private final BookCodesRepository bookCodesRepository;

    @Bean
    public Job bookCodesJob() {
        return new JobBuilder("bookCodesJob", jobRepository)
                .start(bookCodesStep())
                .build();
    }

    @Bean
    public Step bookCodesStep() {
        return new StepBuilder("bookCodesStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    List<KdcCode> codes = kdcCrawler.findAll();
                    bookCodesRepository.saveAll(codes.stream().map(bookCodesMapper::transform).toList());
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }
}
