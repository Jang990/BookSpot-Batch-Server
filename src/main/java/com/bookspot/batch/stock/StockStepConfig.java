package com.bookspot.batch.stock;

import com.bookspot.batch.stock.data.LibraryStockCsvData;
import com.bookspot.batch.stock.reader.StockCsvDataMapper;
import com.bookspot.batch.stock.reader.StockCsvDelimiterTokenizer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@RequiredArgsConstructor
public class StockStepConfig {

    @Bean
    @StepScope
    public FlatFileItemReader<LibraryStockCsvData> bookStockCsvFileReader(Resource resource) {
        return new FlatFileItemReaderBuilder<LibraryStockCsvData>()
                .name("bookStockCsvFileReader")
                .resource(resource)
                .encoding("euc-kr")
                .lineTokenizer(new StockCsvDelimiterTokenizer())
                .fieldSetMapper(new StockCsvDataMapper())
                .linesToSkip(1)
                .build();
    }
}
