package com.bookspot.batch.job.loan;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AggregationCompletedDecider implements JobExecutionDecider {
    protected static final String CREATION_FILE_STEP_NAME = "aggregateBookFileStep";

    protected static final String EXECUTE_ALL = "EXECUTE_ALL";
    protected static final String SKIP_AGGREGATION = "SKIP_AGGREGATION";
    private static final FlowExecutionStatus EXECUTE_ALL_STATUS = new FlowExecutionStatus(EXECUTE_ALL);
    private static final FlowExecutionStatus SKIP_AGGREGATION_STATUS = new FlowExecutionStatus(SKIP_AGGREGATION);

    private final JobExplorer jobExplorer;


    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        List<JobExecution> prevJobExecutions = jobExplorer.getJobExecutions(jobExecution.getJobInstance());
        if(prevJobExecutions.isEmpty())
            return EXECUTE_ALL_STATUS;

        JobExecution lastJobExecution = prevJobExecutions.getFirst();
        return isIsFileAlreadyCreated(lastJobExecution) ? SKIP_AGGREGATION_STATUS : EXECUTE_ALL_STATUS;
    }

    private boolean isIsFileAlreadyCreated(JobExecution lastJobExecution) {
        return lastJobExecution.getStepExecutions().stream()
                .anyMatch(
                        se -> se.getStepName().equals(CREATION_FILE_STEP_NAME)
                                && se.getExitStatus() == ExitStatus.COMPLETED
                );
    }
}
