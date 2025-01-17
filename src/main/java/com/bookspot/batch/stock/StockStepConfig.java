package com.bookspot.batch.stock;

import com.bookspot.batch.stock.data.LibraryStock;
import com.bookspot.batch.stock.data.LibraryStockCsvData;
import com.bookspot.batch.stock.processor.LibraryStockProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class StockStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final FlatFileItemReader<LibraryStockCsvData> bookStockCsvFileReader;
    private final LibraryStockProcessor LibraryStockProcessor;
    private final JdbcBatchItemWriter<LibraryStock> libraryStockWriter;

    @Bean
    public Step libraryStockStep() {
        return new StepBuilder(StockStepConst.STEP_NAME, jobRepository)
                .<LibraryStockCsvData, LibraryStock>chunk(StockStepConst.CHUNK_SIZE, platformTransactionManager)
                .reader(bookStockCsvFileReader)
                .processor(LibraryStockProcessor)
                .writer(libraryStockWriter)
                .build();
    }
}
