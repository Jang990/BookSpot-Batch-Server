package com.bookspot.batch.crawler.naru;

import com.bookspot.batch.crawler.common.CookieKey;
import com.bookspot.batch.crawler.common.JsoupCrawler;
import com.bookspot.batch.crawler.common.CrawlingResult;
import com.bookspot.batch.crawler.common.CrawlingResultUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NaruCrawler {
    private final JsoupCrawler crawler;
    private final CrawlingResultUtils crawlingResultUtils;

    public NaruRequest createRequest(LibraryCode code) {
        CrawlingResult response = crawler.get(NaruSiteConst.LIBRARY_LIST_PATH);

        return new NaruRequest(
                response.getCookie(CookieKey.SESSION_ID),
                crawlingResultUtils.findElementAttribute(
                        response, NaruPageConst.CSS_QUERY_CSRF, NaruPageConst.VALUE_CSRF),
                code.getCode()
        );
    }
}
