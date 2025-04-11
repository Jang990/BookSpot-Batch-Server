package com.bookspot.batch.job.listener;

import com.bookspot.batch.global.file.stock.StockFileManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class StockSyncJobListener implements JobExecutionListener {
    public static final int ISBN_ID_WARMUP_SIZE = 10_000;

    private final StockFileManager stockFileManager;
    private final String sourceDirectoryPath;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        JobExecutionListener.super.beforeJob(jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(!ExitStatus.COMPLETED.equals(jobExecution.getExitStatus()))
            return;

        try {
            stockFileManager.deleteInnerFiles(sourceDirectoryPath);
        } catch (IOException e) {
            // TODO: 알림 기능 필요
            log.error("도서관 책과 관련된 모든 과정은 성공적으로 완료됐지만 관련 파일 제거 실패", e);
            throw new RuntimeException(e);
        }
    }
}
