package com.bookspot.batch.step;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.csv.stock.IsbnValidationProcessor;
import com.bookspot.batch.step.processor.csv.stock.StockProcessor;
import com.bookspot.batch.step.service.memory.bookid.IsbnMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class StockUpdateStepConfig {
    private static final int STOCK_SYNC_CHUNK_SIZE = 5_000;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    private final FlatFileItemReader<StockCsvData> stockCsvFileReader;

    private final IsbnMemoryRepository isbnMemoryRepository;


    @Bean
    public Step stockSyncStep(CompositeItemProcessor<StockCsvData, LibraryStock> stockCompositeItemProcessor) {
        return new StepBuilder("stockSyncStep", jobRepository)
                .<StockCsvData, LibraryStock>chunk(STOCK_SYNC_CHUNK_SIZE, platformTransactionManager)
                .reader(stockCsvFileReader)
                .processor(stockCompositeItemProcessor)
                .writer(stockWriter())
                .build();
    }

    @Bean
    public CompositeItemProcessor<StockCsvData, LibraryStock> stockCompositeItemProcessor(
            IsbnValidationProcessor isbnValidationProcessor,
            StockProcessor stockProcessor) {
        return new CompositeItemProcessor<>(
                List.of(
                        isbnValidationProcessor,
                        stockProcessor
                )
        );
    }

    @Bean
    @StepScope
    public StockProcessor stockProcessor(@Value("#{jobParameters['libraryId']}") Long libraryId) {
        return new StockProcessor(isbnMemoryRepository, libraryId);
    }

    @Bean
    public JdbcBatchItemWriter<LibraryStock> stockWriter() {
        JdbcBatchItemWriter<LibraryStock> writer = new JdbcBatchItemWriterBuilder<LibraryStock>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO library_stock
                        (book_id, library_id, created_at, updated_at)
                        VALUES(?, ?, NOW(), NOW())
                        ON DUPLICATE KEY UPDATE updated_at = NOW();
                        """)
                .itemPreparedStatementSetter(
                        (book, ps) -> {
                            ps.setLong(1, book.getBookId());
                            ps.setLong(2, book.getLibraryId());
                        })
                .assertUpdates(false)
                .build();
        return writer;
    }
}
