package com.bookspot.batch.infra.opensearch;

public class OpenSearchRetryableException extends RuntimeException {
    public OpenSearchRetryableException(Throwable cause) {
        super(cause);
    }
}
