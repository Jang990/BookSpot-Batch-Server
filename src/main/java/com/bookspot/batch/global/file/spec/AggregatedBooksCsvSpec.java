package com.bookspot.batch.global.file.spec;

import com.bookspot.batch.BookSpotFileConst;

public enum AggregatedBooksCsvSpec {
    ISBN13("isbn13"),
    SUBJECT_CODE("subjectCode"),
    LOAN_COUNT("loanCount");

    private static final String DIRECTORY_NAME = "book";
    private static final String FILE_NAME = "unique_books.csv";

    public static final String DIRECTORY_PATH = BookSpotFileConst.ROOT_DIRECTORY.concat("/").concat(DIRECTORY_NAME);
    public static final String FILE_PATH = DIRECTORY_PATH.concat("/").concat(FILE_NAME);

    private final String fieldName;

    AggregatedBooksCsvSpec(String fieldName) {
        this.fieldName = fieldName;
    }

    public String value() {
        return fieldName;
    }

    public static String createLine(String isbn13, Integer subjectCodePrefix, int loanCount) {
        return String.format("\"%s\",\"%s\",\"%d\"",
                isbn13,
                convertNull(subjectCodePrefix),
                loanCount
        );
    }

    private static String convertNull(Integer value) {
        if(value == null)
            return "";
        return String.valueOf(value);
    }
}
