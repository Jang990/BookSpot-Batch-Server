package com.bookspot.batch.step.writer;

import com.bookspot.batch.step.service.memory.bookid.IsbnMemoryRepository;
import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import com.bookspot.batch.step.writer.memory.InMemoryIsbnWriter;
import com.bookspot.batch.step.writer.memory.InMemoryIsbnWriterWithCsv;
import com.bookspot.batch.step.writer.memory.InMemoryIsbnIdWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class InMemoryWriterConfig {
    private final IsbnSet isbnSet;
    private final IsbnMemoryRepository isbnEclipseMemoryRepository;

    @Bean
    public InMemoryIsbnWriter inMemoryIsbnWriter() {
        return new InMemoryIsbnWriter(isbnSet);
    }

    @Bean
    public InMemoryIsbnWriterWithCsv inMemoryIsbnWriterWithCsv() {
        return new InMemoryIsbnWriterWithCsv(isbnSet);
    }

    @Bean
    public InMemoryIsbnIdWriter inMemoryIsbnIdWriter() {
        return new InMemoryIsbnIdWriter(isbnEclipseMemoryRepository);
    }
}
