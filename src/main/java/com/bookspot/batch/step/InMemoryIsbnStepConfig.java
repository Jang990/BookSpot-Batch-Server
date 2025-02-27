package com.bookspot.batch.step;

import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.reader.IsbnReader;
import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class InMemoryIsbnStepConfig {
    public static final int CHUNK_SIZE = 10_000;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final IsbnSet isbnSet;

    @Bean
    public Step inMemoryIsbnClearStep() {
        return new StepBuilder("inMemoryIsbnClearStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    isbnSet.clearAll();
                    return RepeatStatus.FINISHED;
                },platformTransactionManager)
                .build();
    }

    @Bean
    public Step inMemoryIsbnWarmUpStep(
            IsbnReader isbnReader,
            ItemWriter<String> inMemoryIsbnWriter) throws Exception {
        return new StepBuilder("inMemoryIsbnWarmUpStep", jobRepository)
                .<String, String>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(isbnReader)
                .writer(inMemoryIsbnWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ItemWriter<String> inMemoryIsbnWriter() {
        ItemWriterAdapter<String> adapter = new ItemWriterAdapter<>();
        adapter.setTargetObject(isbnSet);
        adapter.setTargetMethod("add");
        return adapter;
    }

    @Bean
    public ItemWriter<StockCsvData> inMemoryCsvIsbnWriter() {
        return chunk -> {
            chunk.getItems().forEach(
                    stockCsv -> isbnSet.add(stockCsv.getIsbn())
            );
        };
    }

    @Bean
    public ItemProcessor<StockCsvData, StockCsvData> inMemoryIsbnFilter() {
        return item -> {
            if(isbnSet.contains(item.getIsbn()))
                return null;
            return item;
        };
    }

}
