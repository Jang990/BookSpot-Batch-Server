package com.bookspot.batch.step;

import com.bookspot.batch.job.stock.StockSyncJobConfig;
import com.bookspot.batch.step.partition.IdRangePartitioner;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class DuplicatedTempStockStepConfig {
    public static final int CHUNK_SIZE = 10_000;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JdbcTemplate jdbcTemplate;

    @Bean
    public Step duplicatedTempStockMasterStep(
            Step duplicatedTempStockStep,
            TaskExecutorPartitionHandler duplicatedTempStockPartitionHandler) throws IOException {
        return new StepBuilder("duplicatedTempStockMasterStep", jobRepository)
                .partitioner(duplicatedTempStockStep.getName(), duplicatedTempStockPartitioner())
                .partitionHandler(duplicatedTempStockPartitionHandler)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler duplicatedTempStockPartitionHandler(
            Step duplicatedTempStockStep,
            TaskExecutor multiTaskPool) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(duplicatedTempStockStep);
        partitionHandler.setTaskExecutor(multiTaskPool);
        partitionHandler.setGridSize(4);
        return partitionHandler;
    }

    @Bean
    @StepScope
    public Partitioner duplicatedTempStockPartitioner() throws IOException {
        return new IdRangePartitioner(jdbcTemplate, StockSyncJobConfig.TEMP_DB_NAME);
    }

    @Bean
    public Step duplicatedTempStockStep() {
        return new StepBuilder("duplicatedTempStockStep", jobRepository)
                .tasklet(duplicatedTempStockTasklet(null, null), transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet duplicatedTempStockTasklet(
            @Value(IdRangePartitioner.MIN_PARAM) Long minId,
            @Value(IdRangePartitioner.MAX_PARAM) Long maxId) {
        // TODO: 재처리 방안 필요.
        return (contribution, chunkContext) -> {

            for (long start = minId; start <= maxId; start += CHUNK_SIZE) {
                long end = Math.min(start + CHUNK_SIZE - 1, maxId);
                String sql = """
                            DELETE temp
                            FROM %s temp
                            INNER JOIN library_stock ls
                              ON ls.book_id = temp.book_id
                             AND ls.library_id = temp.library_id
                            WHERE temp.id BETWEEN ? AND ?
                        """.formatted(StockSyncJobConfig.TEMP_DB_NAME);

                jdbcTemplate.update(sql, start, end);
            }

            return RepeatStatus.FINISHED;
        };
    }
}
