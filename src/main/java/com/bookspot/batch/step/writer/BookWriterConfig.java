package com.bookspot.batch.step.writer;

import com.bookspot.batch.step.writer.book.UniqueBookInfoWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class BookWriterConfig {
    private final DataSource dataSource;

    @Bean
    public UniqueBookInfoWriter bookInfoWriter() {
        return new UniqueBookInfoWriter(dataSource);
    }
}
