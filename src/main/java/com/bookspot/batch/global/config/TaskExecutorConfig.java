package com.bookspot.batch.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TaskExecutorConfig {
    public static final int MULTI_POOL_SIZE = 2;
    public static final int SCHEDULING_POOL_SIZE = 4;
    public static final String SCHEDULING_TASK_POOL_NAME = "schedulingTaskPool";

    @Bean
    public TaskExecutor multiTaskPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(MULTI_POOL_SIZE);
        executor.setMaxPoolSize(MULTI_POOL_SIZE);
        executor.setThreadNamePrefix("multi-csv-thread");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
        executor.initialize();
        return executor;
    }

    @Bean
    public TaskExecutor singleTaskPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("single-csv-thread");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
        executor.initialize();
        return executor;
    }

    @Bean(name = SCHEDULING_TASK_POOL_NAME)
    public TaskExecutor schedulingTaskPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(SCHEDULING_POOL_SIZE);
        executor.setMaxPoolSize(SCHEDULING_POOL_SIZE);
        executor.setThreadNamePrefix("scheduling-thread");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
        executor.initialize();
        return executor;
    }
}
