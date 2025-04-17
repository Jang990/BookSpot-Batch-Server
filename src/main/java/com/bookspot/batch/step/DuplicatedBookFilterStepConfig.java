package com.bookspot.batch.step;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.global.config.TaskExecutorConfig;
import com.bookspot.batch.global.file.stock.StockFilenameUtil;
import com.bookspot.batch.job.stock.DuplicatedBookFilterJobConfig;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.partition.StockCsvPartitionConfig;
import com.bookspot.batch.step.processor.DuplicatedBookIdFilter;
import com.bookspot.batch.step.reader.StockNormalizedFileReader;
import com.bookspot.batch.step.service.memory.isbn.BookIdSet;
import com.bookspot.batch.step.writer.file.stock.StockNormalizeFileWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
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

    @Bean
    public Step duplicatedBookFilterMasterStep(
            Step duplicatedBookFilterStep,
            TaskExecutorPartitionHandler duplicatedBookFilterPartitionHandler) throws IOException {
        return new StepBuilder("duplicatedBookFilterMasterStep", jobRepository)
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
            @Value(DuplicatedBookFilterJobConfig.SOURCE_DIR_PARAM) String root) throws IOException {
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
    public Step duplicatedBookFilterStep(StepLoggingListener stepLoggingListener) {
        return new StepBuilder("duplicatedBookFilterStep", jobRepository)
                .<LibraryStock, LibraryStock>chunk(2_000, transactionManager)
                .reader(normalizedFileReader(null))
                .processor(duplicatedBookIdFilter())
                .writer(duplicatedBookIdWriter(null, null))
                .listener(stepLoggingListener)
                .build();
    }

    @Bean
    @StepScope
    public StockNormalizedFileReader normalizedFileReader(
            @Value(StockCsvPartitionConfig.STEP_EXECUTION_FILE) Resource file) {
        return new StockNormalizedFileReader(file);
    }

    @Bean
    @StepScope
    public DuplicatedBookIdFilter duplicatedBookIdFilter() {
        return new DuplicatedBookIdFilter(new BookIdSet());
    }

    @Bean
    @StepScope
    public StockNormalizeFileWriter duplicatedBookIdWriter(
            @Value(DuplicatedBookFilterJobConfig.OUTPUT_DIR_PARAM) String normalizeDirPath,
            @Value(StockCsvPartitionConfig.STEP_EXECUTION_FILE) Resource file) {
        String outputFile = normalizeDirPath.concat("/")
                .concat(StockFilenameUtil.toFiltered(file.getFilename()))
                .concat(".csv");
        return new StockNormalizeFileWriter(outputFile);
    }
}
