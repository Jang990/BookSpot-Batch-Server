package com.bookspot.batch.job.launcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
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

    public void launchAllWeekly(LocalDate referenceDate) {
        myBatchJobLauncher.launch(
                weeklyTop50BooksJob,
                bookSpotJobParamBuilder.buildTop50BooksJobParams(
                        referenceDate
                )
        );
    }

    public void launchAllMonthly(LocalDate referenceDate) {
        myBatchJobLauncher.launch(
                monthlyTop50BooksJob,
                bookSpotJobParamBuilder.buildTop50BooksJobParams(
                        referenceDate
                )
        );
    }
}
