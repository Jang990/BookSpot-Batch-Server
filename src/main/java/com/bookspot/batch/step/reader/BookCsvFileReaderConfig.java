package com.bookspot.batch.step.reader;

import com.bookspot.batch.step.reader.file.csv.book.BookCsvData;
import com.bookspot.batch.step.reader.file.csv.book.BookCsvDataMapper;
import com.bookspot.batch.step.reader.file.csv.book.BookCsvDelimiterTokenizer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
@Deprecated
public class BookCsvFileReaderConfig {
    private static final String TARGET_PATH = "NL_BO_BOOK_PUB_202410-1.csv";
    private static final Resource SAMPLE_RESOURCE = new PathMatchingResourcePatternResolver().getResource(TARGET_PATH);

    @Bean
    public FlatFileItemReader<BookCsvData> bookCsvFileItemReader() {
        return new FlatFileItemReaderBuilder<BookCsvData>()
                .name("bookCsvFileItemReader")
                .resource(SAMPLE_RESOURCE)
                .encoding("UTF-8")
                .lineTokenizer(new BookCsvDelimiterTokenizer())
                .fieldSetMapper(new BookCsvDataMapper())
                .linesToSkip(1)
                .build();
    }
}
