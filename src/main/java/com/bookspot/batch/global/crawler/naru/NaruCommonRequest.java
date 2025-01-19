package com.bookspot.batch.global.crawler.naru;

import com.bookspot.batch.global.crawler.naru.exception.NaruRequestValidationException;
import org.springframework.util.StringUtils;

import java.util.Map;

public class NaruCommonRequest {
    private static final String HEADER_KEY_JSESSIONID = "JSESSIONID";

    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String BODY_KEY_CSRF_TOKEN = "_csrf";

    private final String jSessionId;
    private final String csrfToken;

    public NaruCommonRequest(String jSessionId, String csrfToken) {
        if (isBlank(jSessionId)
                || isBlank(csrfToken))
            throw new NaruRequestValidationException();

        this.jSessionId = jSessionId;
        this.csrfToken = csrfToken;
    }

    public String getRequestBody() {
        return BODY_KEY_CSRF_TOKEN + KEY_VALUE_DELIMITER + csrfToken;
    }

    public Map<String, String> getHeader() {
        return Map.of(HEADER_KEY_JSESSIONID, jSessionId);
    }

    private boolean isBlank(String jSessionId) {
        return !StringUtils.hasText(jSessionId);
    }

    public String getJSessionId() {
        return jSessionId;
    }

    public String getCsrfToken() {
        return csrfToken;
    }

}
