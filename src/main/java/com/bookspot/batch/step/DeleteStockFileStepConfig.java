package com.bookspot.batch.step;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.global.FileService;
import com.bookspot.batch.global.config.TaskExecutorConfig;
import com.bookspot.batch.global.file.stock.StockFilenameUtil;
import com.bookspot.batch.job.stock.StockSyncJobConfig;
import com.bookspot.batch.step.listener.DeletedStockFileCreator;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.partition.StockCsvPartitionConfig;
import com.bookspot.batch.step.reader.StockNormalizedFileReader;
import com.bookspot.batch.step.writer.ExistsStockChecker;
import lombok.RequiredArgsConstructor;
import org.eclipse.collections.impl.map.mutable.primitive.LongBooleanHashMap;
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
public class DeleteStockFileStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step deleteStockFileMasterStep(
            Step deleteStockFileStep,
            TaskExecutorPartitionHandler deleteStockFilePartitionHandler) throws IOException {
        return new StepBuilder("deleteStockFileMasterStep", jobRepository)
                .partitioner(deleteStockFileStep.getName(), deleteStockFilePartitioner(null))
                .partitionHandler(deleteStockFilePartitionHandler)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler deleteStockFilePartitionHandler(
            Step deleteStockFileStep,
            TaskExecutor multiTaskPool) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(deleteStockFileStep);
        partitionHandler.setTaskExecutor(multiTaskPool);
        partitionHandler.setGridSize(TaskExecutorConfig.MULTI_POOL_SIZE);
        return partitionHandler;
    }

    @Bean
    @StepScope
    public MultiResourcePartitioner deleteStockFilePartitioner(
            @Value(StockSyncJobConfig.DUPLICATED_FILTER_DIR_PARAM) String root) throws IOException {
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
    public Step deleteStockFileStep(
            DeletedStockFileCreator deletedStockFileCreator,
            StockNormalizedFileReader stockNormalizedFileReader,
            ExistsStockChecker existsStockChecker,
            StepLoggingListener stepLoggingListener) {
        return new StepBuilder("deleteStockFileStep", jobRepository)
                .<LibraryStock, LibraryStock>chunk(StockSyncJobConfig.DELETE_FILE_CHUNK_SIZE, transactionManager)
                .reader(stockNormalizedFileReader)
                .writer(existsStockChecker)
                .listener(stepLoggingListener)
                .listener(deletedStockFileCreator)
                .build();
    }

    @Bean
    @StepScope
    public DeletedStockFileCreator deletedStockFileCreator(
            FileService fileService,
            ExistsStockChecker existsStockChecker,
            @Value(StockCsvPartitionConfig.STEP_EXECUTION_FILE) Resource file,
            @Value(StockSyncJobConfig.DELETE_DIR_PARAM) String deleteDir) throws IOException {
        String filePath = deleteDir.concat("/")
                .concat(StockFilenameUtil.toDelete(file.getFilename()))
                .concat(".csv");
        return new DeletedStockFileCreator(
                fileService,
                existsStockChecker,
                filePath
        );
    }

    @Bean
    @StepScope
    public ExistsStockChecker existsStockChecker(
            @Value(StockCsvPartitionConfig.STEP_EXECUTION_FILE) Resource file,
            LongBooleanHashMap bookIdExistsMap) {
        return new ExistsStockChecker(
                StockFilenameUtil.parse(file.getFilename()),
                bookIdExistsMap
        );
    }
}
