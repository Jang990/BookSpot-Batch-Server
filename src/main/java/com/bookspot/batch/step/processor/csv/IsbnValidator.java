package com.bookspot.batch.step.processor.csv;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IsbnValidator {
    private static final List<String> BOOK_ISBN_PREFIX = List.of("979", "978");

    // ex) 8809105873036는 DVD 자료
    public boolean isBookType(String isbn13) {
        return BOOK_ISBN_PREFIX.stream()
                .anyMatch(isbn13::startsWith);
    }

    public boolean isInValid(String isbn13) {
        if(isbn13 == null || isbn13.isBlank() || isbn13.length() != 13)
            return true;

        for (int i = 0; i < isbn13.length(); i++) {
            if (isbn13.charAt(i) < '0' || isbn13.charAt(i) > '9') {
                return true;
            }
        }

        return false;
    }
}
