package com.bookspot.batch.crawler.common;


import org.springframework.stereotype.Service;

@Service
public class CrawlingResultUtils {
    public String findElementAttribute(
            CrawlingResult result,
            String cssQuery,
            String attributeKey) {
        return result.getResponse().selectFirst(cssQuery)
                .attr(attributeKey);
    }
}
