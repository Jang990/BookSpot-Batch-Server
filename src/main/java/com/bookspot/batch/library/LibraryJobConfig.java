package com.bookspot.batch.library;

import com.bookspot.batch.library.data.Library;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class LibraryJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final LibraryItemReader libraryItemReader;

    @Bean
    public Job libraryJob() {
        return new JobBuilder("libraryJob2", jobRepository)
                .start(libraryStep())
//                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step libraryStep() {
        return new StepBuilder("libraryStep", jobRepository)
                .<Library, Library>chunk(LibraryJobConst.LIBRARY_CHUNK_SIZE, platformTransactionManager)
                .reader(libraryItemReader)
                .writer(items -> items.forEach(item -> System.out.println(item.getName())))
                .build();
    }

}
