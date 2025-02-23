package com.bookspot.batch.global.file.spec;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibraryStockCsvSpecTest {

    @Test
    public void CSV_파일_데이터_순서가_변경되지_않으면_순서는_고정돼야_함() {
        List<String> expected = List.of(
                "id",
                "title", "author", "publisher",
                "publicationYear", "isbn", "setIsbn",
                "additionalCode", "volume", "subjectCode",
                "numberOfBooks", "loanCount", "registrationDate"
        );

        assertEquals(expected,
                Arrays.stream(LibraryStockCsvSpec.values())
                        .map(LibraryStockCsvSpec::value)
                        .toList());
    }

}