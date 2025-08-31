package com.bookspot.batch.job.listener.alert;

import com.bookspot.batch.service.alert.AlertMessage;
import com.bookspot.batch.service.alert.SlackAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class AlertJobListener implements JobExecutionListener {
    private final SlackAlertService alertService;
    private final JobAlertMessageConvertor messageFormatter;
    private final Environment env;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (!Arrays.asList(env.getActiveProfiles()).contains("prod")) {
            return;
        }

        AlertMessage alertMessage = messageFormatter.convert(jobExecution);

        if (BatchStatus.COMPLETED.equals(jobExecution.getStatus())) {
            alertService.info(alertMessage);
        } else {
            alertService.error(alertMessage);
        }
    }
}

