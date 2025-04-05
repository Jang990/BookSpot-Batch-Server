package com.bookspot.batch.job.validator.file;

import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class OptionalFilePathValidator implements CustomFilePathValidator {
    @Override
    public boolean isSupported(FilePathType type) {
        return FilePathType.OPTIONAL_FILE.equals(type);
    }

    @Override
    public boolean isValidPath(String pathStr) {
        Path path = Path.of(pathStr);
        if(!Files.exists(path))
            return hasFileExt(pathStr);
        return Files.isRegularFile(path);
    }

    private boolean hasFileExt(String filePathStr) {
        String[] elements = filePathStr.split("\\.");
        String fileExt = elements[elements.length - 1];
        return fileExt.equals("csv") || fileExt.equals("xlsx");
    }
}
