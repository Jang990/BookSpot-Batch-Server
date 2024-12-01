package com.bookspot.batch.crawler.naru;

import com.bookspot.batch.crawler.naru.exception.NaruRequestValidationException;
import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;

import java.util.Map;

@EqualsAndHashCode
public class NaruRequest {
    private static final String HEADER_KEY_JSESSIONID = "JSESSIONID";

    private static final String PAIR_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String BODY_KEY_CSRF_TOKEN = "_csrf";
    private static final String BODY_KEY_LIBRARY_CODE = "libcode";

    private final String jSessionId;
    private final String csrfToken;
    private final String libraryCode;

    public NaruRequest(String jSessionId, String csrfToken, String libraryCode) {
        if (isBlank(jSessionId)
                || isBlank(csrfToken)
                || isBlank(libraryCode))
            throw new NaruRequestValidationException();

        this.jSessionId = jSessionId;
        this.csrfToken = csrfToken;
        this.libraryCode = libraryCode;
    }

    public String getRequestBody() {
        return BODY_KEY_CSRF_TOKEN + KEY_VALUE_DELIMITER + csrfToken
                    + PAIR_DELIMITER
                + BODY_KEY_LIBRARY_CODE + KEY_VALUE_DELIMITER + libraryCode;
    }

    public Map<String, String> getHeader() {
        return Map.of(HEADER_KEY_JSESSIONID, jSessionId);
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
