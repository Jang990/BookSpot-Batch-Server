package com.bookspot.batch.step;

import com.bookspot.batch.job.stock.TEMP_StockSyncJobConfig;
import com.bookspot.batch.step.partition.StockCsvPartitionConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class StockStagingStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JdbcTemplate jdbcTemplate;

    @Bean
    public Step stockStagingMasterStep(
            Step stockStagingStep,
            TaskExecutorPartitionHandler stockStagingPartitionHandler) throws IOException {
        return new StepBuilder("stockStagingMasterStep", jobRepository)
                .partitioner(stockStagingStep.getName(), stockStagingPartitioner(null))
                .partitionHandler(stockStagingPartitionHandler)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler stockStagingPartitionHandler(
            Step stockStagingStep,
            TaskExecutor multiTaskPool) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(stockStagingStep);
        partitionHandler.setTaskExecutor(multiTaskPool);
        return partitionHandler;
    }

    @Bean
    @StepScope
    public MultiResourcePartitioner stockStagingPartitioner(
            @Value(TEMP_StockSyncJobConfig.SOURCE_DIR_PARAM) String root) throws IOException {
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
    public Step stockStagingStep(Tasklet loadToTempTableTasklet) {
        return new StepBuilder("stockStagingStep", jobRepository)
                .tasklet(loadToTempTableTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet loadToTempTableTasklet(
            @Value(StockCsvPartitionConfig.STEP_EXECUTION_FILE) Resource file) {
        return (contribution, chunkContext) -> {
            jdbcTemplate.execute(
                    """
                    LOAD DATA LOCAL INFILE '%s'
                    INTO TABLE %s
                    FIELDS TERMINATED BY ','
                    LINES TERMINATED BY '\\n'
                    (book_id, library_id)
                    """.formatted(
                            file.getFile()
                                    .getPath()
                                    .replace("\\", "/"),
                            TEMP_StockSyncJobConfig.TEMP_DB_NAME
                    )
            );
            return RepeatStatus.FINISHED;
        };
    }
}
