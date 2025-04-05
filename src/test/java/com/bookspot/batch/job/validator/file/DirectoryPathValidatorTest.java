package com.bookspot.batch.job.validator.file;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryPathValidatorTest {
    DirectoryPathValidator validator = new DirectoryPathValidator();

    @Test
    void 디렉토리_경로가_있어야만_통과() {
        assertTrue(validator.isValidPath("src/test/resources/files/valid/existDir"));
    }

    @Test
    void 디렉토리가_존재하지_않으면_실패() {
        assertFalse(validator.isValidPath("src/test/resources/files/valid/non-existDir"));
    }

    @Test
    void 파일은_실패() {
        assertFalse(validator.isValidPath("src/test/resources/files/valid/csv.csv"));
        assertFalse(validator.isValidPath("src/test/resources/files/valid/excel.xlsx"));
    }

}