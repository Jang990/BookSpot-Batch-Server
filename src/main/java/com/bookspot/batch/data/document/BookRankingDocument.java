package com.bookspot.batch.data.document;

import com.bookspot.batch.data.BookCategories;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BookRankingDocument extends BookCommonFields{
    private int rank;
    @JsonProperty("ranking_date")
    private String rankingDate;
    @JsonProperty("loan_increase")
    private int loanIncrease;

    @JsonProperty("ranking_type")
    private RankingType rankingType;

    @JsonProperty("ranking_age")
    private RankingAge rankingAge;

    @JsonProperty("ranking_gender")
    private RankingGender rankingGender;


    public BookRankingDocument(
            String bookId, String isbn13, String title, String author,
            String publisher, Integer publicationYear, LocalDate createdAt,
            Integer subjectCode, BookCategories bookCategories,
            int rank, LocalDate rankingDate, RankingType rankingType,
            RankingAge rankingAge, RankingGender rankingGender, int loanIncrease
    ) {
        super(
                bookId, isbn13, title, author,
                publisher, publicationYear, createdAt,
                subjectCode, bookCategories
        );
        this.rank = rank;
        this.rankingType = rankingType;
        this.rankingDate = rankingDate.toString();
        this.rankingAge = rankingAge;
        this.rankingGender = rankingGender;
        this.loanIncrease = loanIncrease;
    }

    @Override
    public String getDocumentId() {
        return null;
    }
}
