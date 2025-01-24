package com.bookspot.batch.step;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.data.file.csv.LibraryStockCsvData;
import com.bookspot.batch.step.processor.csv.stock.LibraryStockProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class LibraryStockUpdateStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final FlatFileItemReader<LibraryStockCsvData> bookStockCsvFileReader;
    private final LibraryStockProcessor libraryStockProcessor;
    private final JdbcBatchItemWriter<LibraryStock> libraryStockWriter;

    @Bean
    public Step libraryStockUpdateStep() {
        return new StepBuilder("libraryStockUpdateStep", jobRepository)
                .<LibraryStockCsvData, LibraryStock>chunk(StockStepConst.CHUNK_SIZE, platformTransactionManager)
                .reader(bookStockCsvFileReader)
                .processor(libraryStockProcessor)
                .writer(libraryStockWriter)
                .build();
    }
}
