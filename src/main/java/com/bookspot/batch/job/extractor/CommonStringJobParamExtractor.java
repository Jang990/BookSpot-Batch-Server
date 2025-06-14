package com.bookspot.batch.job.extractor;

import com.bookspot.batch.job.BookSpotParentJobConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.step.job.JobParametersExtractor;

import java.time.LocalDate;
import java.util.Map;

public class CommonStringJobParamExtractor implements JobParametersExtractor {
    private final Map<String, String> paramNameMap;

    public static final CommonStringJobParamExtractor EmptyExtractor = new CommonStringJobParamExtractor();

    private CommonStringJobParamExtractor() {
        paramNameMap = Map.of();
    }

    public CommonStringJobParamExtractor(String parentParamName, String childParamName) {
        paramNameMap = Map.of(parentParamName, childParamName);
    }

    public CommonStringJobParamExtractor(Map<String, String> paramNameMap) {
        this.paramNameMap = paramNameMap;
    }

    @Override
    public JobParameters getJobParameters(Job job, StepExecution stepExecution) {
        JobParameters jobParameters = stepExecution.getJobParameters();

        JobParametersBuilder childParamBuilder = new JobParametersBuilder();

        childParamBuilder.addLocalDate(
                BookSpotParentJobConfig.MONTH_PARAM_NAME,
                extractCommonMonth(jobParameters)
        );

        for (String parentParamName : paramNameMap.keySet()) {
            String parentParamValue = jobParameters.getString(parentParamName);
            if (parentParamValue == null)
                throw new IllegalArgumentException("BookSpotParentJob에서 파라미터 %s를 찾을 수 없음".formatted(parentParamName));
            childParamBuilder.addString(paramNameMap.get(parentParamName), parentParamValue);
        }

        childParamBuilder.addString("temp", "12");

        return childParamBuilder.toJobParameters();
    }

    private LocalDate extractCommonMonth(JobParameters jobParameters) {
        LocalDate month = jobParameters.getLocalDate(BookSpotParentJobConfig.MONTH_PARAM_NAME);
        if (month == null)
            throw new IllegalArgumentException(
                    "BookSpotParentJob에서 파라미터 month를 찾을 수 없음"
                            .formatted(BookSpotParentJobConfig.MONTH_PARAM_NAME)
            );
        return month;
    }
}
