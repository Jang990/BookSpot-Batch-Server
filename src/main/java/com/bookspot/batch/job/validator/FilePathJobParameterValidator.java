package com.bookspot.batch.job.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

public class FilePathJobParameterValidator implements JobParametersValidator {
    public static FilePathJobParameterValidator onlyRootDir() {
        return new FilePathJobParameterValidator(true, false);
    }

    public static FilePathJobParameterValidator rootDirAndAggregatedFile() {
        return new FilePathJobParameterValidator(true, true);
    }

    public static final String ROOT_DIR_PATH_PARAM_NAME = "rootDirPath";
    public static final String ROOT_DIR_PATH = "#{jobParameters['rootDirPath']}";

    public static final String AGGREGATED_FILE_PATH_PARAM_NAME = "aggregatedFilePath";
    public static final String AGGREGATED_FILE_PATH = "#{jobParameters['aggregatedFilePath']}";

    private static final String ERROR_MESSAGE_TEMPLATE = "%s 는 필수 JobParameter입니다.";

    private boolean requireRootDirPath;
    private boolean requireAggregatedFilePath;

    private FilePathJobParameterValidator(boolean requireRootDirPath, boolean requireAggregatedFilePath) {
        this.requireRootDirPath = requireRootDirPath;
        this.requireAggregatedFilePath = requireAggregatedFilePath;
    }

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        if (requireAggregatedFilePath)
            validateFilePath(parameters, AGGREGATED_FILE_PATH_PARAM_NAME);


        if (requireRootDirPath)
            validateFilePath(parameters, ROOT_DIR_PATH_PARAM_NAME);
    }

    private void validateFilePath(JobParameters parameters, String parameterName) throws JobParametersInvalidException {
        String filePath = parameters.getString(parameterName);

        if(filePath == null || filePath.isBlank())
            throw new JobParametersInvalidException(ERROR_MESSAGE_TEMPLATE.formatted(parameterName));
    }
}
