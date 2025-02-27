package com.bookspot.batch.step.reader;

import com.bookspot.batch.step.processor.csv.book.YearParser;
import com.bookspot.batch.step.reader.file.csv.stock.StockCsvDataMapper;
import com.bookspot.batch.step.reader.file.csv.stock.StockCsvDelimiterTokenizer;
import com.bookspot.batch.step.writer.file.stock.StockCsvMetadataCreator;
import com.bookspot.batch.data.file.csv.StockCsvData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class StockCsvFileReaderConfig {
    private final YearParser yearParser;

    @Bean
    @StepScope
    public MultiResourceItemReader<StockCsvData> multiStockCsvFileReader() {
        MultiResourceItemReader<StockCsvData> reader = new MultiResourceItemReader<>();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] resources = resolver.getResources(StockCsvMetadataCreator.MULTI_CSV_FILE_PATH);
            reader.setResources(resources);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load files", e);
        }

        reader.setDelegate(new FlatFileItemReaderBuilder<StockCsvData>()
                .name("stockCsvFileReader")
                .encoding("euc-kr")
                .lineTokenizer(new StockCsvDelimiterTokenizer())
                .fieldSetMapper(new StockCsvDataMapper(yearParser))
                .recordSeparatorPolicy(new DefaultRecordSeparatorPolicy())
                .linesToSkip(1)
                .build());
        return reader;
    }
}
