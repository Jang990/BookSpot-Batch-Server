package com.bookspot.batch.job;

import com.bookspot.batch.data.mapper.BookCodeMapper;
import com.bookspot.batch.global.crawler.kdc.KdcCode;
import com.bookspot.batch.global.crawler.kdc.KdcCrawler;
import com.bookspot.batch.step.service.BookCodeRepository;
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
public class BookCodeJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final KdcCrawler kdcCrawler;
    private final BookCodeMapper bookCodeMapper;
    private final BookCodeRepository bookCodeRepository;

    @Bean
    public Job bookCodeJob() {
        return new JobBuilder("bookCodeJob", jobRepository)
                .start(bookCodeStep())
                .build();
    }

    @Bean
    public Step bookCodeStep() {
        return new StepBuilder("bookCodeStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    List<KdcCode> codes = kdcCrawler.findAll();
                    bookCodeRepository.saveAll(codes.stream().map(bookCodeMapper::transform).toList());
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }
}
