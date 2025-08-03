package com.bookspot.batch.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class TaskExecutorConfig {
    public static final int MULTI_POOL_SIZE = 2;
    public static final int SCHEDULING_POOL_SIZE = 2;
    public static final String JOB_LAUNCHER_TASK_POOL_NAME = "jobLauncherTaskPool";

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

    @Bean(name = JOB_LAUNCHER_TASK_POOL_NAME)
    public TaskExecutor jobLauncherTaskPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(MULTI_POOL_SIZE);
        executor.setMaxPoolSize(MULTI_POOL_SIZE);
        executor.setThreadNamePrefix("my-job-launcher-thread");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
        executor.initialize();
        return executor;
    }

    @Bean
    public TaskScheduler schedulingTaskPool() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(SCHEDULING_POOL_SIZE);
        scheduler.setThreadNamePrefix("scheduling-thread");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30); // 안전 종료 대기 시간
        scheduler.initialize();
        return scheduler;
    }
}
