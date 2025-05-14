package com.bookspot.batch;

import com.bookspot.batch.global.properties.files.BookSpotDirectoryProperties;
import com.bookspot.batch.global.properties.files.BookSpotFileProperties;
import com.bookspot.batch.job.BookSyncJobConfig;
import com.bookspot.batch.job.LibrarySyncJobConfig;
import com.bookspot.batch.job.StockFileJobConfig;
import com.bookspot.batch.job.loan.LoanAggregatedJobConfig;
import com.bookspot.batch.job.loan.LoanSyncJobConfig;
import com.bookspot.batch.job.stock.StockNormalizeJobConfig;
import com.bookspot.batch.job.stock.StockSyncJobConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TempRunner implements CommandLineRunner {
    // 임시 코드
    private final JobLauncher jobLauncher;

    private final BookSpotDirectoryProperties directoryProperties;
    private final BookSpotFileProperties fileProperties;

    private final Job librarySyncJob;
    private final Job stockFileJob;
    private final Job bookSyncJob;

    private final Job loanAggregatedJob;
    private final Job loanSyncJob;

    private final Job stockNormalizeJob;
    private final Job stockSyncJob;

    private final Job bookOpenSearchSyncJob;

    /*private final Job bookCodeJob;*/

    @Override
    public void run(String... args) {
        execute(
                librarySyncJob,
                new JobParametersBuilder()
                        .addString(
                                LibrarySyncJobConfig.LIBRARY_FILE_PARAM_NAME,
                                fileProperties.library()
                        )
        );

        execute(
                stockFileJob,
                new JobParametersBuilder()
                        .addString(
                                StockFileJobConfig.DOWNLOAD_DIR_PARAM_NAME,
                                directoryProperties.bookSync()
                        )
        );

        execute(
                bookSyncJob,
                new JobParametersBuilder()
                        .addString(
                                BookSyncJobConfig.SOURCE_DIR_PARAM_NAME,
                                directoryProperties.bookSync()
                        )
        );

        execute(
                loanAggregatedJob,
                new JobParametersBuilder()
                        .addString(
                                LoanAggregatedJobConfig.DIRECTORY_PARAM_NAME,
                                directoryProperties.loanSync()
                        )
                        .addString(
                                LoanAggregatedJobConfig.OUTPUT_FILE_PARAM_NAME,
                                fileProperties.loan()
                        )
        );

        execute(
                loanSyncJob,
                new JobParametersBuilder()
                        .addString(
                                LoanSyncJobConfig.AGGREGATED_FILE_PARAM_NAME,
                                fileProperties.loan()
                        )
        );

        execute(
                stockNormalizeJob,
                new JobParametersBuilder()
                        .addString(
                                StockNormalizeJobConfig.SOURCE_DIR_PARAM_NAME,
                                directoryProperties.stockSync()
                        )
                        .addString(
                                StockNormalizeJobConfig.NORMALIZE_DIR_PARAM_NAME,
                                directoryProperties.normalizedStock()
                        )
                        .addString(
                                StockNormalizeJobConfig.DUPLICATED_FILTER_DIR_PARAM_NAME,
                                directoryProperties.filteredStock()
                        )
        );

        execute(
                stockSyncJob,
                new JobParametersBuilder()
                        .addString(
                                StockSyncJobConfig.SOURCE_DIR_PARAM_NAME,
                                directoryProperties.filteredStock()
                        )
                        .addString(
                                StockSyncJobConfig.DELETE_DIR_PARAM_NAME,
                                directoryProperties.deletedStock()
                        )
        );

        execute(
                bookOpenSearchSyncJob,
                new JobParametersBuilder()
        );
    }

    public void execute(Job job, JobParametersBuilder builder) {
        builder.addLocalDate("month", LocalDate.now().withDayOfMonth(1));
        JobParameters jobParameters = builder.toJobParameters();

        try {
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            if (isCompleted(jobExecution.getExitStatus())) {
                log.info("[{}] 성공적으로 작업 마무리", job.getName());
                return;
            }

            jobExecution.getAllFailureExceptions()
                    .forEach(ex -> log.error("{} 실패 사유: {}", job.getName(), ex.getMessage(), ex));
            throw new JobExecutionException("성공적으로 마치지 못한 Job");
        } catch (JobInstanceAlreadyCompleteException e) {
            log.info("[{}]는 이미 성공한 Job. 파라미터 내용 => {}", job.getName(), jobParameters.getParameters());
        } catch (JobExecutionException e) {
            log.error("[{}]에서 Job 예외 발생. 파라미터 내용 => {}", job.getName(), jobParameters.getParameters());
            throw new RuntimeException(e);
        }
    }

    private boolean isCompleted(ExitStatus exitStatus) {
        return exitStatus.equals(ExitStatus.COMPLETED);
    }
}
