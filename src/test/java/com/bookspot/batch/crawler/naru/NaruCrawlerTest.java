package com.bookspot.batch.crawler.naru;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class NaruCrawlerTest {
    private NaruCrawler crawler = new NaruCrawler();

    @Test
    void 정보나루에_요청을_보낼_수_있는_요소들을_세팅한다() throws IOException {
        LibraryCode code = new LibraryCode("127058");
        NaruRequest request = crawler.createRequest(code);

        assertFalse(request.getJSessionId().isBlank());
        assertFalse(request.getCsrfToken().isBlank());
        assertEquals("127058", request.getLibraryCode());
    }

}