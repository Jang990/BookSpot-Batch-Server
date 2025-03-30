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

    @Bean
    @StepScope
    public MultiResourcePartitioner stockCsvPartitioner(
            @Value(FilePathJobParameterValidator.ROOT_DIR_PATH) String root) throws IOException {
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();


        Path rootPath = Paths.get(root);
        Resource[] resources = Files.list(rootPath) // 루트 디렉토리의 파일 리스트 가져오기
                .filter(Files::isRegularFile) // 파일만 필터링 (디렉토리 제외)
                .map(Path::toFile) // Path -> File 변환
                .map(FileSystemResource::new) // File -> Resource 변환
                .toArray(Resource[]::new);

        partitioner.setKeyName(PARTITIONER_KEY);
        partitioner.setResources(resources);

        return partitioner;
    }

    @Bean
    public TaskExecutor stockCsvTaskPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("stock-csv-thread");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
        executor.initialize();
        return executor;
    }
}
