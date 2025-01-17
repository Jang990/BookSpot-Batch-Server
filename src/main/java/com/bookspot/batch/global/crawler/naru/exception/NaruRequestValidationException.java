package com.bookspot.batch.global.crawler.naru.exception;

public class NaruRequestValidationException extends RuntimeException {
    private static final String message = "정보나루 요청을 생성할 때 잘못된 값이 들어왔습니다.";

    public NaruRequestValidationException() {
        super(message);
    }
}
