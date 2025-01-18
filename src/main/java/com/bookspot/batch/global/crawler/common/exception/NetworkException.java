package com.bookspot.batch.global.crawler.common.exception;

public class NetworkException extends RuntimeException {
    private static final String message = "크롤링 시도 중 네트워크 관련 오류가 발생했습니다.";

    public NetworkException(Throwable cause) {
        super(message, cause);
    }
}
