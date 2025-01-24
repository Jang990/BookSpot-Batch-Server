package com.bookspot.batch.step.processor.csv.stock;

import com.bookspot.batch.step.processor.csv.stock.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LibraryStockProcessorConfig {
    private final BookRepository bookRepository;

    @Bean
    @StepScope
    public LibraryStockProcessor libraryStockProcessor(@Value("#{jobParameters['libraryId']}") long libraryId) {
        return new LibraryStockProcessor(bookRepository, libraryId);
    }
}
