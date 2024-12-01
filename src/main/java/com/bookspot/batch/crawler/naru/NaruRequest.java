package com.bookspot.batch.crawler.naru;

import com.bookspot.batch.crawler.naru.exception.NaruRequestValidationException;
import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;

@EqualsAndHashCode
public class NaruRequest {
    private final String jSessionId;
    private final String csrfToken;
    private final String libraryCode;

    public NaruRequest(String jSessionId, String csrfToken, String libraryCode) {
        if(isBlank(jSessionId)
                || isBlank(csrfToken)
                || isBlank(libraryCode))
            throw new NaruRequestValidationException();

        this.jSessionId = jSessionId;
        this.csrfToken = csrfToken;
        this.libraryCode = libraryCode;
    }

    private boolean isBlank(String jSessionId) {
        return !StringUtils.hasText(jSessionId);
    }

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
