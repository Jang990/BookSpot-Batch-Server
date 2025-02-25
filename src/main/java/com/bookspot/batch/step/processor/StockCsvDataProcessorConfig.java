package com.bookspot.batch.step.processor;

import com.bookspot.batch.data.file.csv.ConvertedStockCsvData;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.csv.stock.BookClassificationProcessor;
import com.bookspot.batch.step.processor.csv.stock.IsbnValidationProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class StockCsvDataProcessorConfig {
    private final IsbnValidationProcessor isbnValidationProcessor;
    private final BookClassificationProcessor bookClassificationProcessor;

    @Bean
    public CompositeItemProcessor<StockCsvData, ConvertedStockCsvData> stockCsvDataProcessor() {
        return new CompositeItemProcessor<>(List.of(
                isbnValidationProcessor,
                bookClassificationProcessor
        ));
    }
}
