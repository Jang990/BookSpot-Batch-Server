package com.bookspot.batch.job.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilePathJobParameterValidator implements JobParametersValidator {
    public static FilePathJobParameterValidator onlyRootDir() {
        return new FilePathJobParameterValidator(true, false);
    }

    public static FilePathJobParameterValidator onlyAggregatedFile() {
        return new FilePathJobParameterValidator(false, true);
    }

    public static FilePathJobParameterValidator rootDirAndAggregatedFile() {
        return new FilePathJobParameterValidator(true, true);
    }

    public static final String ROOT_DIR_PATH_PARAM_NAME = "rootDirPath";
    public static final String ROOT_DIR_PATH = "#{jobParameters['rootDirPath']}";

    public static final String AGGREGATED_FILE_PATH_PARAM_NAME = "aggregatedFilePath";
    public static final String AGGREGATED_FILE_PATH = "#{jobParameters['aggregatedFilePath']}";

    private static final String ERROR_MESSAGE_TEMPLATE = "%s 는 필수 JobParameter입니다.";
    private static final String DIR_ERROR_MESSAGE = "%s는 디렉토리 경로여야 합니다.".formatted(ROOT_DIR_PATH_PARAM_NAME);
    private static final String FILE_ERROR_MESSAGE = "%s는 파일 경로여야 합니다.".formatted(AGGREGATED_FILE_PATH_PARAM_NAME);

    private boolean requireRootDirPath;
    private boolean requireAggregatedFilePath;

    private FilePathJobParameterValidator(boolean requireRootDirPath, boolean requireAggregatedFilePath) {
        this.requireRootDirPath = requireRootDirPath;
        this.requireAggregatedFilePath = requireAggregatedFilePath;
    }

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        if (requireAggregatedFilePath) {
            String filePath = parameters.getString(AGGREGATED_FILE_PATH_PARAM_NAME);
            if(!StringUtils.hasText(filePath))
                throw new JobParametersInvalidException(ERROR_MESSAGE_TEMPLATE.formatted(AGGREGATED_FILE_PATH_PARAM_NAME));
            if(!isFile(filePath))
                throw new JobParametersInvalidException(FILE_ERROR_MESSAGE);
        }


        if (requireRootDirPath) {
            String dirPath = parameters.getString(ROOT_DIR_PATH_PARAM_NAME);
            if(!StringUtils.hasText(dirPath))
                throw new JobParametersInvalidException(ERROR_MESSAGE_TEMPLATE.formatted(ROOT_DIR_PATH_PARAM_NAME));
            if(!isDirectory(dirPath))
                throw new JobParametersInvalidException(DIR_ERROR_MESSAGE);
        }
    }

    private boolean isFile(String pathStr) {
        Path path = Path.of(pathStr);
        if(Files.exists(path))
            return Files.isRegularFile(path);

        return hasFileExt(pathStr);
    }

    private boolean hasFileExt(String filePathStr) {
        String[] elements = filePathStr.split("\\.");
        String fileExt = elements[elements.length - 1];
        return fileExt.equals("csv") || fileExt.equals("xlsx");
    }

    private boolean isDirectory(String pathStr) {
        Path path = Path.of(pathStr);
        return Files.exists(path) && Files.isDirectory(path);
    }


}
