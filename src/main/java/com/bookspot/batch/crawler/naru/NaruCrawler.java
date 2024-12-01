package com.bookspot.batch.crawler.naru;

import com.bookspot.batch.crawler.common.CookieKeyConst;
import com.bookspot.batch.crawler.common.JsoupCrawler;
import com.bookspot.batch.crawler.common.CrawlingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NaruCrawler {
    private final JsoupCrawler crawler;

    public NaruRequest createRequest(LibraryCode code) {
        CrawlingResult result = crawler.get(NaruSiteConst.LIBRARY_LIST_PATH);

        return new NaruRequest(
                result.getCookie(CookieKeyConst.SESSION_ID),
                result.findElementAttribute(
                        NaruPageConst.CSS_QUERY_CSRF, NaruPageConst.VALUE_CSRF),
                code.getCode()
        );
    }
}
