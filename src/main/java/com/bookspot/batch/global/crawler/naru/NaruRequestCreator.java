package com.bookspot.batch.global.crawler.naru;

import com.bookspot.batch.global.crawler.common.CookieKeyConst;
import com.bookspot.batch.global.crawler.common.CrawlingResult;
import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NaruRequestCreator {

    private static final String DOMAIN = "https://data4library.kr/openDataL";
    private static final String CSS_QUERY_CSRF_TOKEN = "input[name=_csrf]";
    private static final String VALUE_CSRF_TOKEN = "value";


    private final JsoupCrawler crawler;


    public NaruCommonRequest create() {
        CrawlingResult result = crawler.get(DOMAIN);

        return new NaruCommonRequest(
                result.getCookie(CookieKeyConst.SESSION_ID),
                result.findElementAttribute(CSS_QUERY_CSRF_TOKEN, VALUE_CSRF_TOKEN)
        );
    }
}
