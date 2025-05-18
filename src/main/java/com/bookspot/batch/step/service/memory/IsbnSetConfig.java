package com.bookspot.batch.step.service.memory;

import com.bookspot.batch.step.service.memory.isbn.IsbnArraySet;
import com.bookspot.batch.step.service.memory.isbn.IsbnHashSet;
import com.bookspot.batch.step.service.memory.isbn.IsbnPrimitiveHashSet;
import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IsbnSetConfig {
    @Bean
    public IsbnSet isbnSet() {
//        return new IsbnHashSet();
//        return new IsbnArraySet();
        return new IsbnPrimitiveHashSet();
    }
}
