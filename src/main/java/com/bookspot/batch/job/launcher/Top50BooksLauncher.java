package com.bookspot.batch.job.launcher;

import com.bookspot.batch.data.document.RankingAge;
import com.bookspot.batch.data.document.RankingGender;
import com.bookspot.batch.data.document.RankingType;
import com.bookspot.batch.global.config.TaskExecutorConfig;
import com.bookspot.batch.step.reader.api.top50.RankingConditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class Top50BooksLauncher {
    private final MyBatchJobLauncher myBatchJobLauncher;
    private final BookSpotJobParamBuilder bookSpotJobParamBuilder;

    private final Job weeklyTop50BooksJob;
    private final Job monthlyTop50BooksJob;

    @Async(TaskExecutorConfig.JOB_LAUNCHER_TASK_POOL_NAME)
    public void launchAllWeekly(LocalDate referenceDate) {
        myBatchJobLauncher.launchSync(
                weeklyTop50BooksJob,
                bookSpotJobParamBuilder.buildTop50BooksJobParams(
                        referenceDate
                )
        );
    }

    @Async(TaskExecutorConfig.JOB_LAUNCHER_TASK_POOL_NAME)
    public void launchAllMonthly(LocalDate referenceDate) {
        myBatchJobLauncher.launchSync(
                monthlyTop50BooksJob,
                bookSpotJobParamBuilder.buildTop50BooksJobParams(
                        referenceDate
                )
        );
    }
}
