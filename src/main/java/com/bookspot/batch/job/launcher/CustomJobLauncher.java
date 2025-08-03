package com.bookspot.batch.job.launcher;

import com.bookspot.batch.global.properties.files.BookSpotDirectoryProperties;
import com.bookspot.batch.global.properties.files.BookSpotFileProperties;
import com.bookspot.batch.job.BookSpotParentJobConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomJobLauncher {
    private final BookSpotDirectoryProperties directoryProperties;
    private final BookSpotFileProperties fileProperties;

    private final MyBatchJobLauncher myBatchJobLauncher;
    private final Job bookSpotParentJob;
    private final Job bookOpenSearchSyncJob;

    public void launchBookOpenSearchSyncJob() {
        myBatchJobLauncher.launch(
                bookOpenSearchSyncJob,
                new JobParametersBuilder()
        );
    }

    public void launchBookSpotJob() {
        myBatchJobLauncher.launch(
                bookSpotParentJob,
                new JobParametersBuilder()
                        .addString(
                                BookSpotParentJobConfig.LIBRARY_FILE_PARAM_NAME,
                                fileProperties.library()
                        )
                        .addString(
                                BookSpotParentJobConfig.STOCK_DIR_PARAM_NAME,
                                directoryProperties.bookSync()
                        )
                        .addString(
                                BookSpotParentJobConfig.DOWNLOAD_DIR_PARAM_NAME,
                                directoryProperties.bookSync()
                        )
                        .addString(
                                BookSpotParentJobConfig.LOAN_OUTPUT_FILE_PARAM_NAME,
                                fileProperties.loan()
                        )
                        .addString(
                                BookSpotParentJobConfig.CLEANSING_DIR_PARAM_NAME,
                                directoryProperties.cleansingStock()
                        )
                        .addString(
                                BookSpotParentJobConfig.DUPLICATED_FILTER_DIR_PARAM_NAME,
                                directoryProperties.filteredStock()
                        )
                        .addString(
                                BookSpotParentJobConfig.DELETE_DIR_PARAM_NAME,
                                directoryProperties.deletedStock()
                        )
        );
    }
}
