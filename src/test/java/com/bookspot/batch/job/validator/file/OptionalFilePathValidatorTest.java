package com.bookspot.batch.job.validator.file;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OptionalFilePathValidatorTest {
    OptionalFilePathValidator validator = new OptionalFilePathValidator();

    @Test
    void 파일_경로만_있어도_통과() {
        assertTrue(validator.isValidPath("src/test/resources/files/valid/nonExisting.csv"));
        assertTrue(validator.isValidPath("src/test/resources/files/valid/nonExisting.xlsx"));
    }

    @Test
    void 실제_파일이_있으면_통과() {
        assertTrue(validator.isValidPath("src/test/resources/files/valid/csv.csv"));
        assertTrue(validator.isValidPath("src/test/resources/files/valid/excel.xlsx"));
    }

    @Test
    void 디렉토리면_실패() {
        assertFalse(validator.isValidPath("src/test/resources/files/valid/existDir"));
    }

    @Test
    void 엑셀_또는_csv가_아닌_파일이면_실패() {
        assertFalse(validator.isValidPath("src/test/resources/files/valid/sss.txt"));
        assertFalse(validator.isValidPath("src/test/resources/files/valid/nonExisting.txt"));
    }
}