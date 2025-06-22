package com.bookspot.batch.step;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.global.config.TaskExecutorConfig;
import com.bookspot.batch.global.file.stock.StockFilenameUtil;
import com.bookspot.batch.job.stock.StockSyncJobConfig;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.listener.alert.AlertStepListener;
import com.bookspot.batch.step.partition.StockCsvPartitionConfig;
import com.bookspot.batch.step.processor.IsbnValidationFilter;
import com.bookspot.batch.step.processor.StockProcessor;
import com.bookspot.batch.step.processor.exception.InvalidIsbn13Exception;
import com.bookspot.batch.step.reader.IsbnIdReader;
import com.bookspot.batch.step.reader.StockCsvFileReaderAndDeleter;
import com.bookspot.batch.step.service.memory.bookid.Isbn13MemoryData;
import com.bookspot.batch.step.service.memory.bookid.IsbnMemoryRepository;
import com.bookspot.batch.step.writer.file.stock.StockCleansingFileWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class StockNormalizeStepConfig {
    public static final String STOCK_CLEANSING_MASTER_STEP = "stockCleansingMasterStep";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final IsbnIdReader isbnIdReader;
    private final IsbnMemoryRepository isbnMemoryRepository;

    private static final int CHUNK_SIZE = 800;

    @Bean
    public Step stockCleansingMasterStep(
            Step stockCleansingStep,
            TaskExecutorPartitionHandler stockCleansingPartitionHandler,
            AlertStepListener alertStepListener
    ) throws IOException {
        return new StepBuilder(STOCK_CLEANSING_MASTER_STEP, jobRepository)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        StepExecutionListener.super.beforeStep(stepExecution);

                        try {
                            isbnIdReader.open(new ExecutionContext());
                            isbnMemoryRepository.init();

                            Isbn13MemoryData data = null;
                            while ((data = isbnIdReader.read()) != null)
                                isbnMemoryRepository.add(data);

                            isbnIdReader.close();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        isbnMemoryRepository.clearMemory();
                        return StepExecutionListener.super.afterStep(stepExecution);
                    }
                })
                .listener(alertStepListener)
                .partitioner(stockCleansingStep.getName(), stockCleansingPartitioner(null))
                .partitionHandler(stockCleansingPartitionHandler)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler stockCleansingPartitionHandler(
            Step stockCleansingStep,
            TaskExecutor multiTaskPool) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(stockCleansingStep);
        partitionHandler.setTaskExecutor(multiTaskPool);
        partitionHandler.setGridSize(TaskExecutorConfig.MULTI_POOL_SIZE);
        return partitionHandler;
    }

    @Bean
    @StepScope
    public MultiResourcePartitioner stockCleansingPartitioner(
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

    @Bean
    public Step stockCleansingStep(
            StockCsvFileReaderAndDeleter stockCsvFileReaderAndDeleter,
            IsbnValidationFilter isbnValidationFilter,
            StockProcessor stockProcessor,
            StockCleansingFileWriter stockCleansingFileWriter,
            StepLoggingListener stepLoggingListener) {
        return new StepBuilder("stockCleansingStep", jobRepository)
                .<StockCsvData, LibraryStock>chunk(CHUNK_SIZE, transactionManager)
                .reader(stockCsvFileReaderAndDeleter)
                .processor(
                        new CompositeItemProcessor<>(
                                List.of(
                                        isbnValidationFilter,
                                        stockProcessor
                                )
                        )
                )
                .writer(stockCleansingFileWriter)
                .listener(stepLoggingListener)
                .faultTolerant()
                .skip(InvalidIsbn13Exception.class)
                .skipLimit(5_000)
                .build();
    }

    @Bean
    @StepScope
    public StockCleansingFileWriter stockCleansingFileWriter(
            @Value(StockCsvPartitionConfig.STEP_EXECUTION_FILE) Resource file,
            @Value(StockSyncJobConfig.CLEANSING_DIR_PARAM) String cleansingDirPath) {
        String outputFile = cleansingDirPath.concat("/")
                .concat(StockFilenameUtil.toCleansing(file.getFilename()))
                .concat(".csv");
        return new StockCleansingFileWriter(outputFile);
    }

    @Bean
    @StepScope
    public StockProcessor stockProcessor(@Value("#{stepExecutionContext['file']}") Resource file) {
        return new StockProcessor(
                isbnMemoryRepository,
                StockFilenameUtil.parse(file.getFilename()).libraryId()
        );
    }
}
