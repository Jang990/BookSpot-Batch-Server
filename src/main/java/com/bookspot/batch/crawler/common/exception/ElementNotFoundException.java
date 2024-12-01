package com.bookspot.batch.crawler.common.exception;

public class ElementNotFoundException extends RuntimeException {
    private static final String message = "크롤링 정보에서 요소를 찾을 수 없습니다.";
    public ElementNotFoundException() {
        super(message);
    }
}
