package com.bookspot.batch.stock;

import com.bookspot.batch.stock.data.LibraryStockCsvData;
import com.bookspot.batch.stock.reader.StockCsvDataMapper;
import com.bookspot.batch.stock.reader.StockCsvDelimiterTokenizer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class StockStepConfig {
    private static final String TARGET_PATH = "부평구립삼산도서관 장서 대출목록 (2024년 12월).csv";
    private static final Resource SAMPLE_RESOURCE = new PathMatchingResourcePatternResolver().getResource(TARGET_PATH);

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Step libraryStockStep() {
        return new StepBuilder(StockStepConst.STEP_NAME, jobRepository)
                .<LibraryStockCsvData, LibraryStockCsvData>chunk(StockStepConst.CHUNK_SIZE, platformTransactionManager)
                .reader(bookStockCsvFileReader())
                .writer(items -> items.forEach(System.out::println))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<LibraryStockCsvData> bookStockCsvFileReader() {
        return new FlatFileItemReaderBuilder<LibraryStockCsvData>()
                .name("bookStockCsvFileReader")
                .resource(SAMPLE_RESOURCE)
                .encoding("euc-kr")
                .lineTokenizer(new StockCsvDelimiterTokenizer())
                .fieldSetMapper(new StockCsvDataMapper())
                .linesToSkip(1)
                .build();
    }
}
