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
import org.springframework.batch.item.ItemWriter;
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
    private static final String INSERT_SQL = """
                INSERT INTO library_stock (book_id, library_id, created_at, updated_at)
                SELECT temp.book_id, temp.library_id, now(), now()
                FROM %s temp
                WHERE temp.id BETWEEN ? AND ?
            """.formatted(StockSyncJobConfig.TEMP_DB_NAME);

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
                .<IdRange, IdRange>chunk(IdRangeReader.FIXED_CHUNK_SIZE, transactionManager)
                .reader(insertedIdReader(null, null))
                .writer(stockInserter())
                .build();
    }

    @Bean
    @StepScope
    public IdRangeReader insertedIdReader(
            @Value(IdRangePartitioner.MIN_PARAM) Long minId,
            @Value(IdRangePartitioner.MAX_PARAM) Long maxId) {
        return new IdRangeReader(minId, maxId, CHUNK_SIZE);
    }

    @Bean
    public ItemWriter<IdRange> stockInserter() {
        return chunk -> {
            for (IdRange item : chunk.getItems())
                jdbcTemplate.update(INSERT_SQL, item.start(), item.end());
        };
    }
}
