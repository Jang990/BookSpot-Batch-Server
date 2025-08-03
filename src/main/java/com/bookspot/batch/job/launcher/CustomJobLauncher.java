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
}
