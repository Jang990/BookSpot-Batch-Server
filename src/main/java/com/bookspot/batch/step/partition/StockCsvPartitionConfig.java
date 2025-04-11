package com.bookspot.batch.step.partition;

import com.bookspot.batch.job.validator.FilePathJobParameterValidator;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StockCsvPartitionConfig {
    public static final String PARTITIONER_KEY = "file";
    public static final String STEP_EXECUTION_FILE = "#{stepExecutionContext['" + StockCsvPartitionConfig.PARTITIONER_KEY + "']}";
}
