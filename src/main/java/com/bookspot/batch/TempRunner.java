package com.bookspot.batch;

import com.bookspot.batch.global.properties.files.BookSpotDirectoryProperties;
import com.bookspot.batch.global.properties.files.BookSpotFileProperties;
import com.bookspot.batch.job.BookSpotParentJobConfig;
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

    private final Job bookSpotParentJob;

    /*private final Job bookCodeJob;*/

    @Override
    public void run(String... args) {
        execute(
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
