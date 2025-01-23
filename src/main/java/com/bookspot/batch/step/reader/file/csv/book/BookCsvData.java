package com.bookspot.batch.step.reader.file.csv.book;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookCsvData {
    private String isbn13;
    private String author;
    private String title;
    private String publicationYear;
    private String subjectCode;
    private String volume; // "상" "중" "하" or "1" "2" "3" ...
}
