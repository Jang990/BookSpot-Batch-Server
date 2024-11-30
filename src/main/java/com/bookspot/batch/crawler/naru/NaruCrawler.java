package com.bookspot.batch.crawler.naru;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class NaruCrawler {
    public NaruRequest createRequest(LibraryCode code) throws IOException {
        Connection.Response response = Jsoup.connect(NaruSiteConst.LIBRARY_LIST_PATH)
                .execute();
        Element csrfToken = response.parse().selectFirst("input[name=_csrf]");

        return new NaruRequest(
                response.cookie("JSESSIONID"),
                csrfToken.attr("value"),
                code.getCode()
        );
    }
}
