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
        private int ranking;
        private String bookname;
        private String authors;
        private String publisher;
        private int publication_year;
        private String isbn13;
        private String addition_symbol; // 03810
        private String vol;
        private double class_no; // 813.62
        private String class_nm; // 문학 > 한국문학 > 소설
//        private String bookImageURL;
//        private String bookDtlUrl;
        private int loan_count;
    }
}