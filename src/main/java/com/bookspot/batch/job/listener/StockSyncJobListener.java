package com.bookspot.batch.job.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
public class StockSyncJobListener implements JobExecutionListener {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        jdbcTemplate.execute("SET GLOBAL local_infile = 1");
        JobExecutionListener.super.beforeJob(jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        jdbcTemplate.execute("SET GLOBAL local_infile = 0");
        JobExecutionListener.super.afterJob(jobExecution);
    }
}
