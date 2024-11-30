package com.bookspot.batch.crawler.common;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class JsoupCrawler {
    private static final int GET_TIMEOUT_MS = 5_000;

    public CrawlingResult get(String url) {
        try {
            Connection.Response response = Jsoup.connect(url)
                    .timeout(GET_TIMEOUT_MS)
                    .execute();
            return new CrawlingResult(response.parse(), response.cookies());
        } catch (IOException e) {
            // TODO : 커스텀 예외 필요
            throw new RuntimeException(e);
        }
    }
}
