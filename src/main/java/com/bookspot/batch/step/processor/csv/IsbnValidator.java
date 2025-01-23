package com.bookspot.batch.step.processor.csv;

import org.springframework.stereotype.Service;

@Service
public class IsbnValidator {
    public boolean isInValid(String isbn13) {
        return isbn13 == null
                || isbn13.isBlank()
                || isbn13.length() != 13;
    }
}
