package com.bookspot.batch.global.file.spec;

public enum AggregatedBooksCsvSpec {
    ISBN13("isbn13"),
    SUBJECT_CODE("subjectCode"),
    LOAN_COUNT("loanCount");

    private final String fieldName;

    AggregatedBooksCsvSpec(String fieldName) {
        this.fieldName = fieldName;
    }

    public String value() {
        return fieldName;
    }
}
