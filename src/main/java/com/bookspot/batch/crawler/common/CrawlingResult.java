package com.bookspot.batch.crawler.common;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;

import java.util.Map;

@RequiredArgsConstructor
public class CrawlingResult {
    private final Document response;
    private final Map<String,String> cookies;

    protected Document getResponse() {
        return response;
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }
}
