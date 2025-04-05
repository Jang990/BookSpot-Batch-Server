package com.bookspot.batch.job.validator.file;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequiredFilePathValidatorTest {
    RequiredFilePathValidator validator = new RequiredFilePathValidator();

    @Test
    void 실제_파일이_있으면_통과() {
        assertTrue(validator.isValidPath("src/test/resources/files/valid/csv.csv"));
        assertTrue(validator.isValidPath("src/test/resources/files/valid/excel.xlsx"));
    }

    @Test
    void 실제파일이_없으면_실패() {
        assertFalse(validator.isValidPath("src/test/resources/files/valid/nonExisting.csv"));
        assertFalse(validator.isValidPath("src/test/resources/files/valid/nonExisting.xlsx"));
    }

    @Test
    void 디렉토리면_실패() {
        assertFalse(validator.isValidPath("src/test/resources/files/valid/existDir"));
    }

}