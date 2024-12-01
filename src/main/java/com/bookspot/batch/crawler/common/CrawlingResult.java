package com.bookspot.batch.crawler.common;

import com.bookspot.batch.crawler.common.exception.ElementNotFoundException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Map;
import java.util.Objects;

public class CrawlingResult {
    private final Document response;
    private final Map<String,String> cookies;

    public CrawlingResult(Document response, Map<String, String> cookies) {
        Objects.requireNonNull(response);
        Objects.requireNonNull(cookies);
        this.response = response;
        this.cookies = cookies;
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

    public String findElementAttribute(
            String cssQuery, String attributeKey) {
        Element element = response.selectFirst(cssQuery);
        if(element == null)
            throw new ElementNotFoundException();

        return element.attr(attributeKey);
    }
}
