package com.bookspot.batch;

import com.bookspot.batch.data.file.csv.StockCsvData;

public class StockCsvDataBuilder {
    private static final String VALID_TITLE = "적당한 제목";
    private static final String VALID_YEAR = "2025";
    private static final String VALID_ISBN = "0000000000000";
    private static final String VALID_SUBJECT_CODE = "613.2163";
    private static final String VALID_AUTHOR = "작가";
    private static final String VALID_PUBLISHER = "출판사";
    private static final String VALID_VOLUME = "2권";

    private String title = VALID_TITLE;
    private String year = VALID_YEAR;
    private String isbn = VALID_ISBN;
    private String subjectCode = VALID_SUBJECT_CODE;
    private String author = VALID_AUTHOR;
    private String publisher = VALID_PUBLISHER;
    private String volume = VALID_VOLUME;


    public StockCsvDataBuilder title(String title) {
        this.title = title;
        return this;
    }

    public StockCsvDataBuilder volume(String volume) {
        this.volume = volume;
        return this;
    }

    public StockCsvDataBuilder year(String year) {
        this.year = year;
        return this;
    }

    public StockCsvDataBuilder isbn(String isbn) {
        this.isbn = isbn;
        return this;
    }

    public StockCsvDataBuilder subjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
        return this;
    }

    public StockCsvData build() {
        return new StockCsvData(title, author, publisher, year, isbn, volume, subjectCode, 0, 0);
    }
}