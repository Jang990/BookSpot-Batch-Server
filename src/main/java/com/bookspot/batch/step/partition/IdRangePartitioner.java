package com.bookspot.batch.step.partition;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class IdRangePartitioner implements Partitioner {
    public static final String MIN_PARAM_NAME = "minId";
    public static final String MIN_PARAM = "#{stepExecutionContext['minId']}";

    public static final String MAX_PARAM_NAME = "maxId";
    public static final String MAX_PARAM = "#{stepExecutionContext['maxId']}";

    private final JdbcTemplate jdbcTemplate;
    private final String tableName;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();

        Long minId = jdbcTemplate.queryForObject("SELECT MIN(id) FROM %s".formatted(tableName), Long.class);
        Long maxId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM %s".formatted(tableName), Long.class);
        Objects.requireNonNull(minId);
        Objects.requireNonNull(maxId);

        long targetSize = (maxId - minId + 1) / gridSize;

        long start = minId;
        long end = start + targetSize - 1;

        for (int i = 0; i < gridSize; i++) {
            ExecutionContext context = new ExecutionContext();
            context.putLong(MIN_PARAM_NAME, start);
            context.putLong(MAX_PARAM_NAME, (i == gridSize - 1) ? maxId : end);
            partitions.put("partition" + i, context);

            start = end + 1;
            end = start + targetSize - 1;
        }

        return partitions;
    }
}
