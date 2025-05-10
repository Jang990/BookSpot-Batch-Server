package com.bookspot.batch.step;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.global.config.TaskExecutorConfig;
import com.bookspot.batch.job.stock.Temp_StockSyncJobConfig;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.partition.StockCsvPartitionConfig;
import com.bookspot.batch.step.processor.ExistsStockFilter;
import com.bookspot.batch.step.reader.StockNormalizedFileReader;
import com.bookspot.batch.step.writer.stock.LibraryStockDeleter;
import com.bookspot.batch.step.writer.stock.LibraryStockWriter;
import lombok.RequiredArgsConstructor;
import org.eclipse.collections.impl.set.mutable.primitive.LongHashSet;
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

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class DeleteStockStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    public static final int CHUNK_SIZE = 1_000;

    @Bean
    public Step deleteStockMasterStep(
            Step deleteStockStep,
            TaskExecutorPartitionHandler deleteStockPartitionHandler) throws IOException {
        return new StepBuilder("deleteStockMasterStep", jobRepository)
                .partitioner(deleteStockStep.getName(), deleteStockPartitioner(null))
                .partitionHandler(deleteStockPartitionHandler)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler deleteStockPartitionHandler(
            Step deleteStockStep,
            TaskExecutor multiTaskPool) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(deleteStockStep);
        partitionHandler.setTaskExecutor(multiTaskPool);
        partitionHandler.setGridSize(TaskExecutorConfig.MULTI_POOL_SIZE);
        return partitionHandler;
    }

    @Bean
    @StepScope
    public MultiResourcePartitioner deleteStockPartitioner(
            @Value(Temp_StockSyncJobConfig.DELETE_DIR_PARAM) String root) throws IOException {
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
    public Step deleteStockStep(
            StockNormalizedFileReader stockNormalizedFileReader,
            StepLoggingListener stepLoggingListener) {
        return new StepBuilder("deleteStockStep", jobRepository)
                .<LibraryStock, LibraryStock>chunk(CHUNK_SIZE, transactionManager)
                .reader(stockNormalizedFileReader)
                .writer(libraryStockDeleter())
                .listener(stepLoggingListener)
                .build();
    }

    @Bean
    @StepScope
    public LibraryStockDeleter libraryStockDeleter() {
        return new LibraryStockDeleter(dataSource);
    }
}
