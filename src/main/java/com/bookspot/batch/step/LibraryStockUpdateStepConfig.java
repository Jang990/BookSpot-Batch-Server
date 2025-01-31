package com.bookspot.batch.step;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.data.file.csv.LibraryStockCsvData;
import com.bookspot.batch.step.processor.csv.stock.LibraryStockProcessor;
import com.bookspot.batch.step.service.IsbnMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class LibraryStockUpdateStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    private final FlatFileItemReader<LibraryStockCsvData> bookStockCsvFileReader;
    private final IsbnMemoryRepository isbnMemoryRepository;

    @Bean
    public Step libraryStockSyncStep() {
        return new StepBuilder("libraryStockSyncStep", jobRepository)
                .<LibraryStockCsvData, LibraryStock>chunk(StockStepConst.CHUNK_SIZE, platformTransactionManager)
                .reader(bookStockCsvFileReader)
                .processor(libraryStockProcessor(null))
                .writer(libraryStockWriter())
                .build();
    }

    @Bean
    @StepScope
    public LibraryStockProcessor libraryStockProcessor(@Value("#{jobParameters['libraryId']}") Long libraryId) {
        return new LibraryStockProcessor(isbnMemoryRepository, libraryId);
    }

    @Bean
    public JdbcBatchItemWriter<LibraryStock> libraryStockWriter() {
        JdbcBatchItemWriter<LibraryStock> writer = new JdbcBatchItemWriterBuilder<LibraryStock>()
                .dataSource(dataSource)
                .sql("""
                        INSERT IGNORE INTO library_stock
                        (book_id, library_id)
                        VALUES(?, ?);
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
