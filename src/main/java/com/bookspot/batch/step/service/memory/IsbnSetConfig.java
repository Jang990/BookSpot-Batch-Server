package com.bookspot.batch.step.service.memory;

import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IsbnSetConfig {
    @Bean
    public IsbnSet isbnSet() {
        return new IsbnSet();
    }
}
