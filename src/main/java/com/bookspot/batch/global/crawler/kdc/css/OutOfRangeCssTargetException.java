package com.bookspot.batch.global.crawler.kdc.css;

public class OutOfRangeCssTargetException extends RuntimeException {
    private static final String message = "CSS Target 범위를 초과했습니다.";
    public OutOfRangeCssTargetException() {
        super(message);
    }
}
