package com.bookspot.batch.step.listener.alert;

import com.bookspot.batch.service.alert.AlertMessage;
import com.bookspot.batch.service.alert.SlackAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class AlertStepListener implements StepExecutionListener {
    private final SlackAlertService alertService;
    private final StepAlertMessageConvertor alertMessageConvertor;
    private final Environment env;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (!Arrays.asList(env.getActiveProfiles()).contains("prod")) {
            return StepExecutionListener.super.afterStep(stepExecution);
        }


        AlertMessage alertMessage = alertMessageConvertor.convert(stepExecution);

        if (BatchStatus.COMPLETED.equals(stepExecution.getStatus())) {
            alertService.info(alertMessage);
        } else {
            alertService.error(alertMessage);
        }

        return StepExecutionListener.super.afterStep(stepExecution);
    }
}
