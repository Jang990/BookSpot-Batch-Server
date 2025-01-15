package com.bookspot.batch.book.reader;

import com.bookspot.batch.book.data.Book;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
public class BookReaderConfig {
    private static final String TARGET_PATH = "NL_BO_BOOK_PUB_202410-1.csv";
    private static final Resource SAMPLE_RESOURCE = new PathMatchingResourcePatternResolver().getResource(TARGET_PATH);

    @Bean
    public FlatFileItemReader<BookCsvData> bookCsvFileItemReader() {
        return new FlatFileItemReaderBuilder<BookCsvData>()
                .name("bookCsvFileItemReader")
                .resource(SAMPLE_RESOURCE)
                .encoding("UTF-8")
                .lineTokenizer(new BookCsvDelimiterTokenizer())
                .fieldSetMapper(new BookCsvDataMapper(yearParser()))
                .linesToSkip(1)
                .build();
    }

    @Bean
    public YearParser yearParser() {
        return new YearParser();
    }
}
