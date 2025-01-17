package com.bookspot.batch.book;

import com.bookspot.batch.book.data.Book;
import com.bookspot.batch.book.reader.BookCsvData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
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
    private final ItemProcessor<BookCsvData, Book> bookCsvProcessor;
    private final JdbcBatchItemWriter<Book> bookWriter;

    @Bean
    public Step bookStep() {
        return new StepBuilder(BookStepConst.STEP_NAME, jobRepository)
                .<BookCsvData, Book>chunk(BookStepConst.CHUNK_SIZE, platformTransactionManager)
                .reader(bookStockCsvFileReader)
                .processor(bookCsvProcessor)
                .writer(bookWriter)
                .build();
    }
}
