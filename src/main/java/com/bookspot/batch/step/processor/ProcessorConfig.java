package com.bookspot.batch.step.processor;

import com.bookspot.batch.step.processor.csv.book.BookClassificationParser;
import com.bookspot.batch.step.processor.csv.book.YearParser;
import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ProcessorConfig {

    @Bean
    public StockCsvToBookConvertor stockCsvToBookConvertor(
            BookClassificationParser classificationParser,
            YearParser yearParser) {
        return new StockCsvToBookConvertor(classificationParser, yearParser);
    }

    @Bean
    public InMemoryIsbnFilter inMemoryIsbnFilter(IsbnSet isbnSet) {
        return new InMemoryIsbnFilter(isbnSet);
    }

}
