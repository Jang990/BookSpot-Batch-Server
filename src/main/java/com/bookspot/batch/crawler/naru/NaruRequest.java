package com.bookspot.batch.crawler.naru;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
public class NaruRequest {
    private final String jSessionId;
    private final String csrfToken;
    private final String libraryCode;

    protected String getJSessionId() {
        return jSessionId;
    }

    protected String getCsrfToken() {
        return csrfToken;
    }

    protected String getLibraryCode() {
        return libraryCode;
    }
}
