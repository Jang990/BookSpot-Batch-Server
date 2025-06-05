package com.bookspot.batch.job.decider;

import com.bookspot.batch.job.JobParameterFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FileCreationStatusDecider implements JobExecutionDecider {
    public static final String EXECUTE_ALL = "EXECUTE_ALL";
    public static final String SKIP_AGGREGATION = "SKIP_AGGREGATION";

    private static final int PREV_JOB_EXECUTION_IDX = 1;
    private static final FlowExecutionStatus EXECUTE_ALL_STATUS = new FlowExecutionStatus(EXECUTE_ALL);
    private static final FlowExecutionStatus SKIP_AGGREGATION_STATUS = new FlowExecutionStatus(SKIP_AGGREGATION);

    private final String fileCreationStepName;
    private final JobExplorer jobExplorer;

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobExecution.getJobInstance());
        if (jobExecutions.isEmpty() ||
                jobExecutions.size() == PREV_JOB_EXECUTION_IDX) {
            log.trace("이전 작업 시도가 없으므로 모든 작업 시도.");
            return EXECUTE_ALL_STATUS;
        }

        JobExecution prevJobExecution = jobExecutions.get(PREV_JOB_EXECUTION_IDX);
        if (isAlreadyCompleted(prevJobExecution)) {
            log.trace("이전에 파일이 성공적으로 생성됐으므로 파일 생성 작업 스킵.");
            return SKIP_AGGREGATION_STATUS;
        } else {
            log.trace("파일이 성공적으로 생성된 적 없으므로 모든 처리 재시도.");
            return EXECUTE_ALL_STATUS;
        }
    }

    private boolean isAlreadyCompleted(JobExecution prevJobExecution) {
        return prevJobExecution.getStepExecutions().stream()
                .anyMatch(
                        se -> se.getStepName().equals(fileCreationStepName)
                                && se.getExitStatus().equals(ExitStatus.COMPLETED)
                );
    }
}
