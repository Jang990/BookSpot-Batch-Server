package com.bookspot.batch.step.reader.api.top50;

import lombok.Data;
import java.util.List;

@Data
public class WeeklyTop50ResponseSpec {

    private Response response;

    @Data
    public static class Response {
        private Request request;
        private int resultNum;
        private int numFound;
        private List<DocWrapper> docs;
    }

    @Data
    public static class Request {
        private String startDt;
        private String endDt;
        private int pageNo;
        private int pageSize;
    }

    @Data
    public static class DocWrapper {
        private Doc doc;
    }

    @Data
    public static class Doc {
        private int no;
        private String ranking;
        private String bookname;
        private String authors;
        private String publisher;
        private String publication_year;
        private String isbn13;
        private String addition_symbol;
        private String vol;
        private String class_no;
        private String class_nm;
        private String bookImageURL;
        private String bookDtlUrl;
        private String loan_count;
    }
}