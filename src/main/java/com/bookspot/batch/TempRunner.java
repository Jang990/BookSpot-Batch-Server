package com.bookspot.batch;

import com.bookspot.batch.global.properties.files.BookSpotDirectoryProperties;
import com.bookspot.batch.global.properties.files.BookSpotFileProperties;
import com.bookspot.batch.job.BookSpotParentJobConfig;
import com.bookspot.batch.job.launcher.MyBatchJobLauncher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TempRunner implements CommandLineRunner {
    // 임시 코드
    private final MyBatchJobLauncher myBatchJobLauncher;

    private final BookSpotDirectoryProperties directoryProperties;
    private final BookSpotFileProperties fileProperties;

    private final Job bookSpotParentJob;

    //    private final Job bookCodeJob;
    private final Job bookOpenSearchSyncJob;

    @Override
    public void run(String... args) {
//        execute(bookCodeJob, new JobParametersBuilder().addString("abc", "abc"));
//        execute(bookOpenSearchSyncJob, new JobParametersBuilder());
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
