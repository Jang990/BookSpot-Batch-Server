package com.bookspot.batch.job.temp;

import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.reader.TempStockCsvFileReader;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class TempPartitionJobConfig {
    private static final String JOB_NAME = "tempBookSyncPartitionedJob";
    private static final int POOL_SIZE = 10;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job tempBookSyncPartitionedJob(Step tempPartitionMasterStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(tempPartitionMasterStep)
                .build();
    }


    @Bean
    public Step tempPartitionMasterStep(
            Step tempBookSyncStep,
            MultiResourcePartitioner stockCsvPartitioner,
            TaskExecutorPartitionHandler stockCsvPartitionHandler) {
        return new StepBuilder("tempPartitionStep", jobRepository)
                .partitioner(tempBookSyncStep.getName(), stockCsvPartitioner)
                .partitionHandler(stockCsvPartitionHandler)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler stockCsvPartitionHandler(Step tempBookSyncStep, TaskExecutor stockCsvTaskPool) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(tempBookSyncStep);
        partitionHandler.setTaskExecutor(stockCsvTaskPool);
        return partitionHandler;
    }

    @Bean
    public Step tempBookSyncStep(
            TempStockCsvFileReader tempStockCsvFileReader,
            CompositeItemProcessor<StockCsvData, ConvertedUniqueBook> bookSyncProcessor,
            CompositeItemWriter<ConvertedUniqueBook> bookSyncItemWriter) {
        return new StepBuilder("tempBookSyncStep", jobRepository)
                .<StockCsvData, ConvertedUniqueBook>chunk(5_000, platformTransactionManager)
                .reader(tempStockCsvFileReader)
                .processor(bookSyncProcessor)
                .writer(bookSyncItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public TempStockCsvFileReader tempStockCsvFileReader(
            @Value("#{stepExecutionContext['file']}") Resource file) throws Exception {
        return new TempStockCsvFileReader(file);
    }
}
