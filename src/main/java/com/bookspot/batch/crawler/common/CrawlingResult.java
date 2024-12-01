package com.bookspot.batch.crawler.common;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;

import java.util.Map;

@RequiredArgsConstructor
public class CrawlingResult {
    private final Document response;
    private final Map<String,String> cookies;

    public String getCookie(String key) {
        return cookies.get(key);
    }

    public String findElementAttribute(
            String cssQuery, String attributeKey) {
        return response.selectFirst(cssQuery)
                .attr(attributeKey);
    }
}
