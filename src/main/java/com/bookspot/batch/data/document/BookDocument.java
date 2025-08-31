package com.bookspot.batch.data.document;

import com.bookspot.batch.data.BookCategories;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class BookDocument extends BookCommonFields{
    @JsonProperty("library_ids")
    private List<String> libraryIdsArrayString;

    @JsonProperty("loan_count")
    private int loanCount;

    @JsonProperty("monthly_loan_increase")
    private int monthlyLoanIncrease;

    public BookDocument(
            String bookId, String isbn13, String title,
            String author, String publisher,
            int loanCount, int monthlyLoanIncrease,
            Integer subjectCode, Integer publicationYear,
            List<String> libraryIds, BookCategories bookCategories,
            LocalDate createdAt
    ) {
        super(
                bookId, isbn13, title, author,
                publisher, publicationYear, createdAt,
                subjectCode, bookCategories
        );
        this.libraryIdsArrayString = libraryIds;
        this.loanCount = loanCount;
        this.monthlyLoanIncrease = monthlyLoanIncrease;
    }
}
