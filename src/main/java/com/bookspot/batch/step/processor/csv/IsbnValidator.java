package com.bookspot.batch.step.processor.csv;

import org.springframework.stereotype.Service;

@Service
public class IsbnValidator {
    public boolean isInValid(String isbn13) {
        if(isbn13 == null || isbn13.isBlank() || isbn13.length() != 13)
            return true;

        if(isInvalidPrefix(isbn13))
            return true;

        for (int i = 0; i < isbn13.length(); i++) {
            if (isbn13.charAt(i) < '0' || isbn13.charAt(i) > '9') {
                return true;
            }
        }

        return false;
    }

    // 8809105873036같은 DVD도 제외된다.
    private boolean isInvalidPrefix(String isbn13) {
        return !(isbn13.startsWith("979") || isbn13.startsWith("978"));
    }
}
