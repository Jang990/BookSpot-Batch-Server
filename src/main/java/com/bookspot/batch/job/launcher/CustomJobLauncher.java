package com.bookspot.batch.job.launcher;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomJobLauncher {
    private final MyBatchJobLauncher myBatchJobLauncher;
    private final BookSpotJobParamBuilder paramBuilder;
    private final Job bookSpotParentJob;
    private final Job bookOpenSearchSyncJob;
    private final Job top50BooksJob;

    private final LocalDateResolver localDateResolver;

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

    public void launchTop50Books() {
        myBatchJobLauncher.launch(
                top50BooksJob,
                paramBuilder.buildTop50BooksJobParams(
                        localDateResolver.resolveMondayOfWeek()
                )
        );

    }
}
