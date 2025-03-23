package com.bookspot.batch.step;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.global.file.StockCsvMetadataHelper;
import com.bookspot.batch.step.processor.IsbnValidationFilter;
import com.bookspot.batch.step.processor.StockProcessor;
import com.bookspot.batch.step.reader.StockCsvFileReader;
import com.bookspot.batch.step.service.memory.bookid.IsbnMemoryRepository;
import com.bookspot.batch.step.writer.StockWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class StockSyncStepConfig {
    private static final int STOCK_SYNC_CHUNK_SIZE = 5_000;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final IsbnMemoryRepository isbnMemoryRepository;

    @Bean
    public Step stockSyncPartitionMasterStep(
            Step stockSyncStep,
            MultiResourcePartitioner stockCsvPartitioner,
            TaskExecutorPartitionHandler stockSyncPartitionHandler) {
        return new StepBuilder("stockSyncPartitionMasterStep", jobRepository)
                .partitioner(stockSyncStep.getName(), stockCsvPartitioner)
                .partitionHandler(stockSyncPartitionHandler)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler stockSyncPartitionHandler(Step stockSyncStep, TaskExecutor stockCsvTaskPool) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(stockSyncStep);
        partitionHandler.setTaskExecutor(stockCsvTaskPool);
        return partitionHandler;
    }


    @Bean
    public Step stockSyncStep(
            StockCsvFileReader stockCsvFileReader,
            CompositeItemProcessor<StockCsvData, LibraryStock> stockCompositeItemProcessor,
            StockWriter stockWriter) {
        return new StepBuilder("stockSyncStep", jobRepository)
                .<StockCsvData, LibraryStock>chunk(STOCK_SYNC_CHUNK_SIZE, platformTransactionManager)
                .reader(stockCsvFileReader)
                .processor(stockCompositeItemProcessor)
                .writer(stockWriter)
                .build();
    }

    @Bean
    public CompositeItemProcessor<StockCsvData, LibraryStock> stockCompositeItemProcessor(
            IsbnValidationFilter isbnValidationFilter,
            StockProcessor stockProcessor) {
        return new CompositeItemProcessor<>(
                List.of(
                        isbnValidationFilter,
                        stockProcessor
                )
        );
    }

    @Bean
    @StepScope
    public StockProcessor stockProcessor(@Value("#{stepExecutionContext['file']}") Resource file) {
        return new StockProcessor(
                isbnMemoryRepository,
                StockCsvMetadataHelper.parseLibraryId(file)
        );
    }

    @Bean
    public StockWriter stockWriter(DataSource dataSource) {
        return new StockWriter(dataSource);
    }
}
