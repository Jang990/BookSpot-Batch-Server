package com.bookspot.batch.step.reader.file.csv.stock;

enum LibraryStockCsvSpec {
    ID("id"),
    TITLE("title"),
    AUTHOR("author"),
    PUBLISHER("publisher"),
    PUBLICATION_YEAR("publicationYear"),
    ISBN("isbn"),
    SET_ISBN("setIsbn"),
    ADDITIONAL_CODE("additionalCode"),
    VOLUME("volume"),
    SUBJECT_CODE("subjectCode"),
    NUMBER_OF_BOOKS("numberOfBooks"),
    LOAN_COUNT("loanCount"),
    REGISTRATION_DATE("registrationDate");

    private final String fieldName;

    LibraryStockCsvSpec(String fieldName) {
        this.fieldName = fieldName;
    }

    public String value() {
        return fieldName;
    }
}
