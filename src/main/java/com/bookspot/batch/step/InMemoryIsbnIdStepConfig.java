package com.bookspot.batch.step;

import com.bookspot.batch.step.reader.IsbnIdReader;
import com.bookspot.batch.step.service.memory.bookid.Isbn13MemoryData;
import com.bookspot.batch.step.service.memory.bookid.IsbnMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class InMemoryIsbnIdStepConfig {
    public static final int WARM_UP_CHUNK_SIZE = 10_000;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    private final IsbnMemoryRepository isbnEclipseMemoryRepository;

    @Bean
    public Step inMemoryIsbnIdClearStep() {
        return new StepBuilder("inMemoryIsbnIdClearStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    isbnEclipseMemoryRepository.clearMemory();
                    return RepeatStatus.FINISHED;
                },platformTransactionManager)
                .build();
    }

    @Bean
    public Step inMemoryIsbnIdWarmUpStep(IsbnIdReader isbnIdReader) throws Exception {
        return new StepBuilder("inMemoryIsbnIdWarmUpStep", jobRepository)
                .<Isbn13MemoryData, Isbn13MemoryData>chunk(WARM_UP_CHUNK_SIZE, platformTransactionManager)
                .reader(isbnIdReader)
                .writer(inMemoryIsbnIdWriter())
                    .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ItemWriter<Isbn13MemoryData> inMemoryIsbnIdWriter() {
        ItemWriterAdapter<Isbn13MemoryData> adapter = new ItemWriterAdapter<>();
        adapter.setTargetObject(isbnEclipseMemoryRepository);
        adapter.setTargetMethod("add");
        return adapter;
    }
}
