package com.bookspot.batch.step.processor;

import com.bookspot.batch.step.processor.csv.StockCsvToBookConvertor;
import com.bookspot.batch.step.processor.csv.book.BookClassificationParser;
import com.bookspot.batch.step.processor.csv.book.YearParser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ProcessorConfig {
    private final BookClassificationParser classificationParser;
    private final YearParser yearParser;

    @Bean
    public StockCsvToBookConvertor stockCsvToBookConvertor() {
        return new StockCsvToBookConvertor(classificationParser, yearParser);
    }
}
