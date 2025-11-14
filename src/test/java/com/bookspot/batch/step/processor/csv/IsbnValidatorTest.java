package com.bookspot.batch.step.processor.csv;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class IsbnValidatorTest {
    IsbnValidator isbnValidator = new IsbnValidator();

    @ParameterizedTest
    @ValueSource(strings = {
            "", // 빈 문자열
            "1234567890", // 13자리 미만
            "12345678901234567890", // 13자리 초과
            "1234s67890123" // 문자열 포함
    })
    void 잘못된_ISBN13_체크(String invalidIsbn13) {
        assertTrue(isbnValidator.isInValid(invalidIsbn13));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // 13자리 숫자조합
            "1234567890123",
            "2040240202032",
            "9788936434120"
    })
    void 올바른_ISBN13_체크(String validIsbn13) {
        assertFalse(isbnValidator.isInValid(validIsbn13));
    }
}