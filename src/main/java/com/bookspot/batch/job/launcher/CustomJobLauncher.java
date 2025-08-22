package com.bookspot.batch.job.launcher;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CustomJobLauncher {
    private final MyBatchJobLauncher myBatchJobLauncher;
    private final BookSpotJobParamBuilder paramBuilder;
    private final Job bookSpotParentJob;
    private final Job bookOpenSearchSyncJob;

    private final Job weeklyTop50BooksJob;
    private final Job monthlyTop50BooksJob;
    private final Job dailySyncTop50BooksJob;

    public void launchBookOpenSearchSyncJob() {
        myBatchJobLauncher.launch(
                bookOpenSearchSyncJob,
                paramBuilder.buildOpenSearchParams()
        );
    }

    public void launchBookSpotJob() {
        myBatchJobLauncher.launch(
                bookSpotParentJob,
                paramBuilder.buildBookSpotParams()
        );
    }

    public void launchTop50BooksOfWeek(LocalDate referenceDate) {
        myBatchJobLauncher.launch(
                weeklyTop50BooksJob,
                paramBuilder.buildTop50BooksJobParams(referenceDate)
        );
    }

    public void launchTop50BooksOfMonth(LocalDate referenceDate) {
        myBatchJobLauncher.launch(
                monthlyTop50BooksJob,
                paramBuilder.buildTop50BooksJobParams(referenceDate)
        );
    }

    public void launchTop50BooksDailySync(LocalDate now) {
        myBatchJobLauncher.launch(
                dailySyncTop50BooksJob,
                paramBuilder.buildDailySyncTop50BooksJobParams(now)
        );
    }
}
