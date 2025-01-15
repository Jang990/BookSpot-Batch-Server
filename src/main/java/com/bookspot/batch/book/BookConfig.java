package com.bookspot.batch.book;

import com.bookspot.batch.book.data.Book;
import com.bookspot.batch.book.processor.BookCsvProcessor;
import com.bookspot.batch.book.reader.BookCsvData;
import com.bookspot.batch.stock.data.LibraryStockCsvData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class BookConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final FlatFileItemReader<BookCsvData> bookStockCsvFileReader;
    private final BookCsvProcessor bookCsvProcessor;

    @Bean
    public Step bookStep() {
        return new StepBuilder(BookStepConst.STEP_NAME, jobRepository)
                .<BookCsvData, Book>chunk(BookStepConst.CHUNK_SIZE, platformTransactionManager)
                .reader(bookStockCsvFileReader)
                .processor(bookCsvProcessor)
                .writer(items -> items.forEach(System.out::println))
                .build();
    }
}
