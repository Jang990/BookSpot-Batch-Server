package com.bookspot.batch.job.validator.file;

import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class RequiredFilePathValidator implements CustomFilePathValidator {
    @Override
    public boolean isSupported(FilePathType type) {
        return FilePathType.REQUIRED_FILE.equals(type);
    }

    @Override
    public boolean isValidPath(String pathStr) {
        Path path = Path.of(pathStr);
        if(!Files.exists(path))
            return false;
        return Files.isRegularFile(path);
    }
}
