package com.bookspot.batch.job.listener.alert;

import com.bookspot.batch.service.alert.AlertBody;
import com.bookspot.batch.service.alert.AlertMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JobAlertMessageConvertor {
    private static final String JOB_TITLE_FORMAT = "[JOB] jobExecutionId=%d jobName=%s 결과";
    public AlertMessage convert(JobExecution execution) {
        List<AlertBody> bodies = List.of(
                new AlertBody("상태", execution.getStatus().toString()),
                new AlertBody("시작 시각", execution.getStartTime().toString()),
                new AlertBody("종료 시각", execution.getEndTime().toString()),
                new AlertBody("실패 이유", getFailureMessages(execution))
        );

        return new AlertMessage(toTitle(execution), bodies);
    }

    @NotNull
    private String toTitle(JobExecution execution) {
        return JOB_TITLE_FORMAT.formatted(
                execution.getJobId(),
                execution.getJobInstance().getJobName()
        );
    }

    private String getFailureMessages(JobExecution execution) {
        return execution.getAllFailureExceptions().stream()
                .map(Throwable::getMessage)
                .collect(Collectors.joining("\n"));
    }

}
