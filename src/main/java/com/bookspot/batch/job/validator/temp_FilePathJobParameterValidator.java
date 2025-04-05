package com.bookspot.batch.job.validator;

import com.bookspot.batch.job.validator.file.CustomFilePathValidators;
import com.bookspot.batch.job.validator.file.FilePathType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class temp_FilePathJobParameterValidator implements JobParametersValidator {
    private final CustomFilePathValidators validators;
    private final Map<String, FilePathType> requiredJobParams;
    private static final String NOT_FOUND_PARAM_MESSAGE_TEMPLATE = "%s 는 필수 JobParameter입니다.";

    public static temp_FilePathJobParameterValidator of(CustomFilePathValidators validators, Map<String, FilePathType> requireParams) {
        return new temp_FilePathJobParameterValidator(validators, requireParams);
    }

    public static temp_FilePathJobParameterValidator REQUIRED_FILE(CustomFilePathValidators validators, String paramName) {
        return new temp_FilePathJobParameterValidator(validators, Map.of(paramName, FilePathType.REQUIRED_FILE));
    }

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        for (Map.Entry<String, FilePathType> requiredJobParam : requiredJobParams.entrySet()) {
            String path = parameters.getString(requiredJobParam.getKey());
            if(path == null)
                throw new JobParametersInvalidException(notFoundMessage(requiredJobParam.getKey()));
            if(!validators.valid(requiredJobParam.getValue(), path))
                throw new JobParametersInvalidException(exceptionMessage(requiredJobParam));
        }
    }

    private String exceptionMessage(Map.Entry<String, FilePathType> entry) {
        return switch (entry.getValue()) {
            case REQUIRED_DIRECTORY -> "%s는 디렉토리 경로여야 합니다.".formatted(entry.getKey());
            case REQUIRED_FILE -> "%s는 존재하는 파일 경로여야 합니다.".formatted(entry.getKey());
            case OPTIONAL_FILE -> "%s는 파일 경로여야 합니다.".formatted(entry.getKey());
            default -> "잘못된 파라미터";
        };
    }

    private static String notFoundMessage(String key) {
        return NOT_FOUND_PARAM_MESSAGE_TEMPLATE.formatted(key);
    }
}
