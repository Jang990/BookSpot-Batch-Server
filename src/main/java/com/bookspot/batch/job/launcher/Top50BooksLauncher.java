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

    private final Job top50BooksJob;

    @Async(TaskExecutorConfig.JOB_LAUNCHER_TASK_POOL_NAME)
    public void launchAllWeekly(LocalDate referenceDate) {
        process(referenceDate, RankingType.WEEKLY);
    }

    @Async(TaskExecutorConfig.JOB_LAUNCHER_TASK_POOL_NAME)
    public void launchAllMonthly(LocalDate referenceDate) {
        process(referenceDate, RankingType.MONTHLY);
    }

    private void process(LocalDate referenceDate, RankingType type) {
        for (RankingGender gender : RankingGender.values()) {
            for (RankingAge age : RankingAge.values()) {
                myBatchJobLauncher.launchSync(
                        top50BooksJob,
                        bookSpotJobParamBuilder.buildTop50BooksJobParams(
                                referenceDate,
                                new RankingConditions(
                                        type,
                                        gender, age
                                )
                        )
                );
                log.info("기간:{}, 성별: {}, 나이: {} 대출 top 50 도서 Job 완료", type, gender, age);
            }
        }
    }
}
