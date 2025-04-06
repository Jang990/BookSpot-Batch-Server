package com.bookspot.batch.step;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.job.StockSyncJobConfig;
import com.bookspot.batch.job.validator.FilePathJobParameterValidator;
import com.bookspot.batch.step.listener.InvalidIsbn13LoggingListener;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.partition.StockCsvPartitionConfig;
import com.bookspot.batch.step.processor.IsbnValidationFilter;
import com.bookspot.batch.step.processor.StockProcessor;
import com.bookspot.batch.step.processor.exception.InvalidIsbn13Exception;
import com.bookspot.batch.step.reader.StockCsvFileReader;
import com.bookspot.batch.step.service.memory.bookid.IsbnMemoryRepository;
import com.bookspot.batch.step.writer.StockWriter;
import com.bookspot.batch.global.file.stock.StockFilenameUtil;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class StockSyncStepConfig {
    private static final int STOCK_SYNC_CHUNK_SIZE = 5_000;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final IsbnMemoryRepository isbnMemoryRepository;
    private final StepLoggingListener stepLoggingListener;
    private final InvalidIsbn13LoggingListener invalidIsbn13LoggingListener;

    @Bean
    public Step stockSyncPartitionMasterStep(
            Step stockSyncStep,
            TaskExecutorPartitionHandler stockSyncPartitionHandler) throws IOException {
        return new StepBuilder("stockSyncPartitionMasterStep", jobRepository)
                .partitioner(stockSyncStep.getName(), stockCsvPartitioner(null))
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
            StockProcessor stockProcessor,
            StockWriter stockWriter) {
        return new StepBuilder("stockSyncStep", jobRepository)
                .<StockCsvData, LibraryStock>chunk(STOCK_SYNC_CHUNK_SIZE, platformTransactionManager)
                .reader(stockCsvFileReader)
                .processor(stockProcessor)
                .writer(stockWriter)
                .listener(stepLoggingListener)
                .listener(invalidIsbn13LoggingListener)
                .faultTolerant()
                .skip(InvalidIsbn13Exception.class)
                .skipLimit(50_000)
                .build();
    }

    @Bean
    @StepScope
    public StockProcessor stockProcessor(@Value("#{stepExecutionContext['file']}") Resource file) {
        return new StockProcessor(
                isbnMemoryRepository,
                StockFilenameUtil.parse(file.getFilename()).libraryId()
        );
    }

    @Bean
    public StockWriter stockWriter(DataSource dataSource) {
        return new StockWriter(dataSource);
    }

    @Bean
    @StepScope
    public MultiResourcePartitioner stockCsvPartitioner(
            @Value(StockSyncJobConfig.SOURCE_DIR_PARAM) String root) throws IOException {
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();

        Path rootPath = Paths.get(root);
        Resource[] resources = Files.list(rootPath) // 루트 디렉토리의 파일 리스트 가져오기
                .filter(Files::isRegularFile) // 파일만 필터링 (디렉토리 제외)
                .map(Path::toFile) // Path -> File 변환
                .map(FileSystemResource::new) // File -> Resource 변환
                .toArray(Resource[]::new);

        partitioner.setKeyName(StockCsvPartitionConfig.PARTITIONER_KEY);
        partitioner.setResources(resources);

        return partitioner;
    }
}
