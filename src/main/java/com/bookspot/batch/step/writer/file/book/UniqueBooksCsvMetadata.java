package com.bookspot.batch.step.writer.file.book;

import com.bookspot.batch.BookSpotFileConst;

public class UniqueBooksCsvMetadata {
    public static final String DIRECTORY_NAME = "book";
    public static final String FILE_NAME = "unique_books.csv";

    public static final String DIRECTORY_PATH = BookSpotFileConst.ROOT_DIRECTORY.concat("/").concat(DIRECTORY_NAME);
    public static final String FILE_PATH = DIRECTORY_PATH.concat("/").concat(FILE_NAME);

    public static String createLine(String isbn13, String subjectCode, int loanCount) {
        return String.format("\"%s\",\"%s\",\"%s\"",
                isbn13,
                escapeCsv(subjectCode),
                escapeCsv(String.valueOf(loanCount))
        );
    }

    private static String escapeCsv(String value) {
        if (value == null) return "";
        return value.contains(",") ? "\"" + value.replace("\"", "\"\"") + "\"" : value;
    }
}
