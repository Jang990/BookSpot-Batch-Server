package com.bookspot.batch.job.validator.file;

import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DirectoryPathValidator implements CustomFilePathValidator {
    @Override
    public boolean isSupported(FilePathType type) {
        return FilePathType.REQUIRED_DIRECTORY.equals(type);
    }

    @Override
    public boolean isValidPath(String pathStr) {
        Path path = Path.of(pathStr);
        return Files.exists(path) && Files.isDirectory(path);
    }
}
