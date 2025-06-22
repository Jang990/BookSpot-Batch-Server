package com.bookspot.batch.job.listener.alert;

import com.bookspot.batch.service.alert.AlertBody;
import com.bookspot.batch.service.alert.AlertMessage;
import com.bookspot.batch.service.alert.SlackAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AlertJobListener implements JobExecutionListener {
    private final SlackAlertService alertService;
    private final JobAlertMessageConvertor messageFormatter;

    @Override
    public void afterJob(JobExecution jobExecution) {
        AlertMessage alertMessage = messageFormatter.convert(jobExecution);

        if (BatchStatus.COMPLETED.equals(jobExecution.getStatus())) {
            alertService.info(alertMessage);
        } else {
            alertService.error(alertMessage);
        }
    }

    private AlertMessage toAlertMessage(JobExecution execution) {
        String title = "[JOB] " + execution.getJobInstance().getJobName() + " 결과";

        List<AlertBody> bodies = List.of(
                new AlertBody("상태", execution.getStatus().name()),
                new AlertBody("시작 시각", execution.getStartTime().toString()),
                new AlertBody("종료 시각", execution.getEndTime().toString()),
                new AlertBody("실패 이유", getFailureMessages(execution))
        );

        return new AlertMessage(title, bodies);
    }

    private String getFailureMessages(JobExecution execution) {
        return execution.getAllFailureExceptions().stream()
                .map(Throwable::getMessage)
                .collect(Collectors.joining("\n"));
    }
}

