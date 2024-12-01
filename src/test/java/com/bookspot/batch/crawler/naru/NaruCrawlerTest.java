package com.bookspot.batch.crawler.naru;

import com.bookspot.batch.crawler.common.JsoupCrawler;
import com.bookspot.batch.crawler.common.CrawlingResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NaruCrawlerTest {
    @Mock JsoupCrawler jsoupCrawler;
    @InjectMocks
    NaruCrawler naruCrawler;

    @Test
    void 정보나루에_요청을_보낼_수_있는_요소들을_세팅한다() {
        CrawlingResult crawlingResult = mock(CrawlingResult.class);
        when(jsoupCrawler.get(anyString())).thenReturn(crawlingResult);
        LibraryCode code = new LibraryCode("127058");
        when(crawlingResult.getCookie(anyString())).thenReturn("MySessionId");
        when(crawlingResult.findElementAttribute(anyString(), anyString())).thenReturn("CSRF_TOKEN_VALUE");


        NaruRequest request = naruCrawler.createRequest(code);

        assertEquals("127058", request.getLibraryCode());
        assertEquals("MySessionId", request.getJSessionId());
        assertEquals("CSRF_TOKEN_VALUE", request.getCsrfToken());
    }

}