package com.bookspot.batch.step;

import com.bookspot.batch.data.IdRange;
import com.bookspot.batch.job.stock.StockSyncJobConfig;
import com.bookspot.batch.step.partition.IdRangePartitioner;
import com.bookspot.batch.step.reader.IdRangeReader;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
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
public class StockUpdateStepConfig {
    public static final int CHUNK_SIZE = 10_000;
    private static final String UPDATE_SQL = """
                UPDATE library_stock ls
                INNER JOIN %s temp
                    ON ls.book_id = temp.book_id AND ls.library_id = temp.library_id
                SET ls.updated_at = NOW()
                WHERE temp.id BETWEEN ? AND ?
            """.formatted(StockSyncJobConfig.TEMP_DB_NAME);

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JdbcTemplate jdbcTemplate;

    @Bean
    public Step stockUpdateMasterStep(
            Step stockUpdateStep,
            TaskExecutorPartitionHandler stockUpdatePartitionHandler) throws IOException {
        return new StepBuilder("stockUpdateMasterStep", jobRepository)
                .partitioner(stockUpdateStep.getName(), stockUpdatePartitioner())
                .partitionHandler(stockUpdatePartitionHandler)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler stockUpdatePartitionHandler(
            Step stockUpdateStep,
            TaskExecutor multiTaskPool) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(stockUpdateStep);
        partitionHandler.setTaskExecutor(multiTaskPool);
        partitionHandler.setGridSize(4);
        return partitionHandler;
    }

    @Bean
    @StepScope
    public Partitioner stockUpdatePartitioner() throws IOException {
        return new IdRangePartitioner(jdbcTemplate, StockSyncJobConfig.TEMP_DB_NAME);
    }

    @Bean
    public Step stockUpdateStep() {
        return new StepBuilder("stockUpdateStep", jobRepository)
                .<IdRange, IdRange>chunk(IdRangeReader.FIXED_CHUNK_SIZE, transactionManager)
                .reader(updatedStockIdRangeReader(null, null))
                .writer(stockUpdatedAtUpdater())
                .build();
    }

    @Bean
    @StepScope
    public IdRangeReader updatedStockIdRangeReader(
            @Value(IdRangePartitioner.MIN_PARAM) Long minId,
            @Value(IdRangePartitioner.MAX_PARAM) Long maxId) {
        return new IdRangeReader(minId, maxId, CHUNK_SIZE);
    }

    @Bean
    public ItemWriter<IdRange> stockUpdatedAtUpdater() {
        return chunk -> {
            for (IdRange item : chunk.getItems())
                jdbcTemplate.update(UPDATE_SQL, item.start(), item.end());
        };
    }
}
