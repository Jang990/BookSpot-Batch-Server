package com.bookspot.batch.step;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.global.FileService;
import com.bookspot.batch.global.config.TaskExecutorConfig;
import com.bookspot.batch.global.file.stock.StockFilenameUtil;
import com.bookspot.batch.job.stock.StockSyncJobConfig;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.listener.alert.AlertStepListener;
import com.bookspot.batch.step.partition.StockCsvPartitionConfig;
import com.bookspot.batch.step.processor.DuplicatedBookIdFilter;
import com.bookspot.batch.step.reader.CleansingStockFileReader;
import com.bookspot.batch.step.reader.CleansingStockFileReaderAndDeleter;
import com.bookspot.batch.step.service.memory.isbn.BookIdSet;
import com.bookspot.batch.step.writer.file.stock.StockCleansingFileWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
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

@Configuration
@RequiredArgsConstructor
public class DuplicatedBookFilterStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private static final int CHUNK_SIZE = 1500;

    @Bean
    public Step duplicatedBookFilterMasterStep(
            Step duplicatedBookFilterStep,
            AlertStepListener alertStepListener,
            TaskExecutorPartitionHandler duplicatedBookFilterPartitionHandler) throws IOException {
        return new StepBuilder("duplicatedBookFilterMasterStep", jobRepository)
                .listener(alertStepListener)
                .partitioner(duplicatedBookFilterStep.getName(), duplicatedBookFilterPartitioner(null))
                .partitionHandler(duplicatedBookFilterPartitionHandler)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler duplicatedBookFilterPartitionHandler(
            Step duplicatedBookFilterStep,
            TaskExecutor multiTaskPool) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(duplicatedBookFilterStep);
        partitionHandler.setTaskExecutor(multiTaskPool);
        partitionHandler.setGridSize(TaskExecutorConfig.MULTI_POOL_SIZE);
        return partitionHandler;
    }

    @Bean
    @StepScope
    public MultiResourcePartitioner duplicatedBookFilterPartitioner(
            @Value(StockSyncJobConfig.CLEANSING_DIR_PARAM) String root) throws IOException {
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
    public Step duplicatedBookFilterStep(
            CleansingStockFileReader cleansingStockFileReaderAndDeleter,
            StepLoggingListener stepLoggingListener
    ) {
        return new StepBuilder("duplicatedBookFilterStep", jobRepository)
                .<LibraryStock, LibraryStock>chunk(CHUNK_SIZE, transactionManager)
                .reader(cleansingStockFileReaderAndDeleter)
                .processor(duplicatedBookIdFilter())
                .writer(duplicatedBookIdWriter(null, null))
                .listener(stepLoggingListener)
                .build();
    }

    @Bean
    @StepScope
    public CleansingStockFileReader cleansingStockFileReader(
            @Value(StockCsvPartitionConfig.STEP_EXECUTION_FILE) Resource file
    ) {
        return new CleansingStockFileReader(file);
    }

    @Bean
    @StepScope
    public CleansingStockFileReader cleansingStockFileReaderAndDeleter(
            @Value(StockCsvPartitionConfig.STEP_EXECUTION_FILE) Resource file,
            @Value("#{stepExecution}") StepExecution stepExecution,
            FileService fileService
    ) {
        return new CleansingStockFileReaderAndDeleter(file, stepExecution, fileService);
    }

    @Bean
    @StepScope
    public DuplicatedBookIdFilter duplicatedBookIdFilter() {
        return new DuplicatedBookIdFilter(new BookIdSet());
    }

    @Bean
    @StepScope
    public StockCleansingFileWriter duplicatedBookIdWriter(
            @Value(StockSyncJobConfig.DUPLICATED_FILTER_DIR_PARAM) String filteredDirPath,
            @Value(StockCsvPartitionConfig.STEP_EXECUTION_FILE) Resource file) {
        String outputFile = filteredDirPath.concat("/")
                .concat(StockFilenameUtil.toFiltered(file.getFilename()))
                .concat(".csv");
        return new StockCleansingFileWriter(outputFile);
    }
}
