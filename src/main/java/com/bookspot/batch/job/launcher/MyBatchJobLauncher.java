package com.bookspot.batch.job.launcher;

import com.bookspot.batch.global.config.TaskExecutorConfig;
import com.bookspot.batch.job.BookSpotParentJobConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyBatchJobLauncher {
    private final JobLauncher jobLauncher;

    @Async(TaskExecutorConfig.SCHEDULING_TASK_POOL_NAME)
    public void launch(Job job, JobParametersBuilder builder) {
        LocalDate now = LocalDate.now();
        builder.addLocalDate(
                BookSpotParentJobConfig.MONTH_PARAM_NAME,
                LocalDate.of(now.getYear(), now.getMonth(), 1)
        );

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
