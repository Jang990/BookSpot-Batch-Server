package com.bookspot.batch.crawler.common;

import java.util.Collections;
import java.util.Map;

public record RequestData(String url, Map<String, String> cookies, String body) {
    public RequestData(String url) {
        this(url, Collections.EMPTY_MAP, null);
    }

    public RequestData(String url, Map<String, String> cookies, String body) {
        this.url = url;
        this.cookies = cookies;
        this.body = body;
    }
}
