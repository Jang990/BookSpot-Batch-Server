package com.bookspot.batch.step.reader.file.csv.book;

import com.bookspot.batch.step.reader.file.csv.book.BookCsvSpec;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookCsvSpecTest {

    @Test
    public void CSV_파일_데이터_순서가_변경되지_않으면_순서는_고정돼야_함() {
        List<String> expected = List.of(
                "controlNumber", "authorName", "volumeName",
                "publicationYear", "classificationNumber", "bookSymbolNumber",
                "titleName", "libraryCode", "isbn13Number",
                "representativeBook", "registerNumber", "incomeFlagName",
                "manageFlagName", "mediaFlagName", "utilizationLimitFlagName",
                "utilizationTargetFlagName", "accompanyDataName", "singleVolumeIsbn",
                "singleVolumeIsbnAdditionalSymbolName", "classificationSymbolFlagName", "volumeSymbolName",
                "duplicateCopySymbolName", "registerDate", "isbn13OriginalNumber",
                "masterLibraryCode", "volumeExists", "setIsbnChanged",
                "volumeOriginalName", "titleSubstituteName", "kdcName",
                "bookClassificationCode", "bookLocationCode"
        );

        assertEquals(expected,
                Arrays.stream(BookCsvSpec.values())
                        .map(BookCsvSpec::value)
                        .toList());
    }

}