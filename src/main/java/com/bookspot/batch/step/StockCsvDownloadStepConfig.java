package com.bookspot.batch.step;

import com.bookspot.batch.data.LibraryForFileParsing;
import com.bookspot.batch.data.crawler.StockFileData;
import com.bookspot.batch.step.processor.crawler.StockFilePathParser;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class StockCsvDownloadStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    /*
    private final LibraryStockProcessor LibraryStockProcessor;
    private final JdbcBatchItemWriter<LibraryStock> libraryStockWriter;*/

    private final JdbcPagingItemReader<LibraryForFileParsing> libraryForFileParsingReader;
    private final StockFilePathParser stockFilePathParser;
    private final ItemWriter<StockFileData> stockFileDownloaderWriter;

    /*@Bean
    public Step libraryStockStep() {
        return new StepBuilder(StockStepConst.STEP_NAME, jobRepository)
                .<LibraryStockCsvData, LibraryStock>chunk(StockStepConst.CHUNK_SIZE, platformTransactionManager)
                .reader(multiBookStockCsvFileReader)
                .processor(LibraryStockProcessor)
                .writer(libraryStockWriter)
                .build();
    }*/

    @Bean
    public Step stockCsvDownloadStep() {
        return new StepBuilder(StockStepConst.DOWNLOAD_STEP_NAME, jobRepository)
                .<LibraryForFileParsing, StockFileData>chunk(StockStepConst.DOWNLOAD_CHUNK_SIZE, platformTransactionManager)
                .reader(libraryForFileParsingReader)
                .processor(stockFilePathParser)
                .writer(stockFileDownloaderWriter)
                .build();
    }

}
