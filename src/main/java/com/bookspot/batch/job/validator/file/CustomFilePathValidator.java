package com.bookspot.batch.job.validator.file;

import java.util.List;

public interface CustomFilePathValidator {
    public static final List<CustomFilePathValidator> list = List.of(

    );

    boolean isSupported(FilePathType type);

    boolean isValidPath(String pathStr);
}
