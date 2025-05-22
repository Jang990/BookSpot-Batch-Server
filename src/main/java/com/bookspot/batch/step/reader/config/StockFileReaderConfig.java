package com.bookspot.batch.step.reader.config;

import com.bookspot.batch.step.partition.StockCsvPartitionConfig;
import com.bookspot.batch.step.reader.*;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;


@Configuration
@RequiredArgsConstructor
public class StockFileReaderConfig {

    @Bean
    @StepScope
    public StockCsvFileReader stockCsvFileReader(
            @Value(StockCsvPartitionConfig.STEP_EXECUTION_FILE) Resource file) throws Exception {
        return new StockCsvFileReader(file);
    }

    @Bean
    @StepScope
    public StockCsvFileReaderAndDeleter stockCsvFileReaderAndDeleter(
            @Value(StockCsvPartitionConfig.STEP_EXECUTION_FILE) Resource file) throws Exception {
        return new StockCsvFileReaderAndDeleter(file);
    }
}
