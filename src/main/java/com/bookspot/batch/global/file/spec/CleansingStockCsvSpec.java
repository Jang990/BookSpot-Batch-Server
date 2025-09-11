package com.bookspot.batch.global.file.spec;

import com.bookspot.batch.data.LibraryStock;
import org.springframework.batch.item.file.transform.FieldSet;

public enum CleansingStockCsvSpec {
    BOOK_ID("bookId"),
    LIBRARY_ID("libraryId"),
    SUBJECT_CODE("subjectCode");

    private final String fieldName;

    CleansingStockCsvSpec(String fieldName) {
        this.fieldName = fieldName;
    }

    public String value() {
        return fieldName;
    }

    public static LibraryStock readLine(FieldSet fieldSet) {
        long libraryId = fieldSet.readLong(LIBRARY_ID.value());
        long bookId = fieldSet.readLong(BOOK_ID.value());
        String subjectCode = fieldSet.readString(SUBJECT_CODE.value());
        return new LibraryStock(
                libraryId,
                bookId,
                subjectCode.isBlank() ? null : subjectCode
        );
    }

    public static String createLine(long bookId, long libraryId, String subjectCode) {
        return String.format("%d,%d,%s", bookId, libraryId, subjectCode == null ? "" : subjectCode);
    }
}
