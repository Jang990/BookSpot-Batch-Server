package com.bookspot.batch.crawler.common;

import com.bookspot.batch.crawler.common.exception.NetworkException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class JsoupCrawler {
    private static final int TIMEOUT_MS = 5_000;

    public CrawlingResult get(String url) {
        try {
            Connection.Response response = Jsoup.connect(url)
                    .timeout(TIMEOUT_MS)
                    .execute();
            return new CrawlingResult(response.parse(), response.cookies());
        } catch (IOException e) {
            throw new NetworkException(e);
        }
    }

    public CrawlingResult post(RequestData requestData) {
        try {
            Connection.Response response = Jsoup.connect(requestData.url())
                    .cookies(requestData.cookies())
                    .requestBody(requestData.body())
                    .method(Connection.Method.POST)
                    .execute();
            return new CrawlingResult(response.parse(), response.cookies());
        } catch (IOException e) {
            throw new NetworkException(e);
        }
    }

}
