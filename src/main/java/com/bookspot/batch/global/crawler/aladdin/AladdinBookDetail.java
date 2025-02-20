package com.bookspot.batch.global.crawler.aladdin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AladdinBookDetail {
    private String isbn;
    private String image;
    private String title;
    private String subTitle;
    private String author;
    private String publisher;
    private String description;
    private String tableOfContents;
    private LocalDate publishedDate;
    private int pageCount;
}
