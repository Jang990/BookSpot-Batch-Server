package com.bookspot.batch.global;

import com.bookspot.batch.job.validator.FilePathJobParameterValidator;
import org.springframework.batch.core.JobParametersBuilder;

public class JobParameterHelper {
    public static JobParametersBuilder addRootDirPath(JobParametersBuilder builder, String path) {
        builder.addString(FilePathJobParameterValidator.ROOT_DIR_PATH_PARAM_NAME, path);
        return builder;
    }

    public static JobParametersBuilder addAggregatedFilePath(JobParametersBuilder builder, String path) {
        builder.addString(FilePathJobParameterValidator.AGGREGATED_FILE_PATH_PARAM_NAME, path);
        return builder;
    }
}
