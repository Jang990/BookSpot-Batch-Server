package com.bookspot.batch.step.service.memory;

import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class IsbnSetConfig {
    @Bean
    @Scope("prototype")
    public IsbnSet isbnSet() {
        return new IsbnSet();
    }
}
