package com.bookspot.batch.step.writer;

import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import com.bookspot.batch.step.writer.memory.InMemoryIsbnWriter;
import com.bookspot.batch.step.writer.memory.InMemoryIsbnWriterWithCsv;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class InMemoryWriterConfig {
    private final IsbnSet isbnSet;

    @Bean
    public InMemoryIsbnWriter inMemoryIsbnWriter() {
        return new InMemoryIsbnWriter(isbnSet);
    }

    @Bean
    public InMemoryIsbnWriterWithCsv inMemoryIsbnWriterWithCsv() {
        return new InMemoryIsbnWriterWithCsv(isbnSet);
    }
}
