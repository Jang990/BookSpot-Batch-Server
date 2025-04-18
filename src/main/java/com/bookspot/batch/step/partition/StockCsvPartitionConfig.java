package com.bookspot.batch.step.partition;

import org.springframework.context.annotation.Configuration;


@Configuration
public class StockCsvPartitionConfig {
    public static final String PARTITIONER_KEY = "file";
    public static final String STEP_EXECUTION_FILE = "#{stepExecutionContext['" + StockCsvPartitionConfig.PARTITIONER_KEY + "']}";
}
