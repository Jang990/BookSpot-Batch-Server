package com.bookspot.batch.job.validator.file;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomFilePathValidators {
    private final List<CustomFilePathValidator> list;

    public boolean valid(FilePathType type, String path) {
        for (CustomFilePathValidator customFilePathValidator : list) {
            if(customFilePathValidator.isSupported(type))
                return customFilePathValidator.isValidPath(path);
        }

        throw new UnsupportedOperationException();
    }
}
