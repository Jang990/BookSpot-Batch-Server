package com.bookspot.batch.global.crawler.naru;

import com.bookspot.batch.global.crawler.naru.exception.NaruRequestValidationException;
import org.springframework.util.StringUtils;

public class NaruDetailRequest extends NaruCommonRequest {
    private static final String PAIR_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String BODY_KEY_CSRF_TOKEN = "_csrf";
    private static final String BODY_KEY_LIBRARY_CODE = "libcode";

    private final String libraryCode;

    public NaruDetailRequest(String jSessionId, String csrfToken, String libraryCode) {
        super(jSessionId, csrfToken);
        if (isBlank(libraryCode))
            throw new NaruRequestValidationException();

        this.libraryCode = libraryCode;
    }

    public String getRequestBody() {
        return BODY_KEY_CSRF_TOKEN + KEY_VALUE_DELIMITER + getCsrfToken()
                    + PAIR_DELIMITER
                + BODY_KEY_LIBRARY_CODE + KEY_VALUE_DELIMITER + libraryCode;
    }

    private boolean isBlank(String jSessionId) {
        return !StringUtils.hasText(jSessionId);
    }


    protected String getLibraryCode() {
        return libraryCode;
    }
}
