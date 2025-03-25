package com.bookspot.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TempRunner implements CommandLineRunner {
    // 임시 코드
    private final JobLauncher jobLauncher;

    private final Job bookCodeJob;

    private final Job librarySyncJob;
    private final Job stockFileJob;

    private final Job bookLoanCountSyncJob;
    private final Job bookElasticSearchSyncJob;

    private final Job bookSyncPartitionedJob;
    private final Job stockSyncJob;

    @Override
    public void run(String... args) throws Exception {
        /*jobLauncher.run(bookCodesJob, new JobParametersBuilder()
                .addLocalDateTime("tempParam", LocalDateTime.now())
                .toJobParameters());*/

        /*jobLauncher.run(bookSyncPartitionedJob, new JobParametersBuilder()
                .addString("rootDirPath", StockCsvMetadataCreator.DIRECTORY_NAME)
                .toJobParameters());*/

        /*jobLauncher.run(stockSyncJob, new JobParametersBuilder()
                .addString("rootDirPath", StockCsvMetadataCreator.DIRECTORY_NAME)
                .toJobParameters());*/

        /*jobLauncher.run(librarySyncJob, new JobParametersBuilder()
                .addLocalDateTime("tempParam", LocalDateTime.now())
                .toJobParameters());*/

        /*jobLauncher.run(stockFileJob, new JobParametersBuilder()
                .addLocalDateTime("tempParam", LocalDateTime.now())
                .toJobParameters());*/

        /*jobLauncher.run(bookElasticSearchSyncJob, new JobParametersBuilder()
                .addLocalDateTime("tempParam", LocalDateTime.now())
                .toJobParameters());*/

        /*tempBookJobs.run();
        jobLauncher.run(bookLoanCountSyncJob, new JobParametersBuilder()
                .addLocalDateTime("tempParam", LocalDateTime.now())
                .toJobParameters());
        tempStockJobs.run();*/
    }
}
