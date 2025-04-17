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
public class StockInsertStepConfig {
    public static final int CHUNK_SIZE = 10_000;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JdbcTemplate jdbcTemplate;

    @Bean
    public Step stockInsertMasterStep(
            Step stockInsertStep,
            TaskExecutorPartitionHandler stockInsertPartitionHandler) throws IOException {
        return new StepBuilder("stockInsertMasterStep", jobRepository)
                .partitioner(stockInsertStep.getName(), stockInsertPartitioner())
                .partitionHandler(stockInsertPartitionHandler)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler stockInsertPartitionHandler(
            Step stockInsertStep,
            TaskExecutor multiTaskPool) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(stockInsertStep);
        partitionHandler.setTaskExecutor(multiTaskPool);
        partitionHandler.setGridSize(4);
        return partitionHandler;
    }

    @Bean
    @StepScope
    public Partitioner stockInsertPartitioner() throws IOException {
        return new IdRangePartitioner(jdbcTemplate, StockSyncJobConfig.TEMP_DB_NAME);
    }

    @Bean
    public Step stockInsertStep() {
        return new StepBuilder("stockInsertStep", jobRepository)
                .tasklet(stockInsertTasklet(null, null), transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet stockInsertTasklet(
            @Value(IdRangePartitioner.MIN_PARAM) Long minId,
            @Value(IdRangePartitioner.MAX_PARAM) Long maxId) {
        // TODO: 재처리 방안 필요.
        return (contribution, chunkContext) -> {

            for (long start = minId; start <= maxId; start += CHUNK_SIZE) {
                long end = Math.min(start + CHUNK_SIZE - 1, maxId);
                String sql = """
                            INSERT INTO library_stock (book_id, library_id, created_at, updated_at)
                            SELECT temp.book_id, temp.library_id, now(), now()
                            FROM %s temp
                            WHERE temp.id BETWEEN ? AND ?
                        """.formatted(StockSyncJobConfig.TEMP_DB_NAME);

                jdbcTemplate.update(sql, start, end);
            }

            return RepeatStatus.FINISHED;
        };
    }
}
