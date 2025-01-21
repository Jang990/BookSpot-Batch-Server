package com.bookspot.batch.global.crawler.naru;

import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import com.bookspot.batch.global.crawler.common.CrawlingResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NaruCrawlerTest {
    @Mock JsoupCrawler jsoupCrawler;
    @InjectMocks
    NaruCrawler naruCrawler;

//    @Test
    void 정보나루_실제_요청() {
        NaruCrawler naru = new NaruCrawler(new JsoupCrawler());
        naru.findCurrentBooksFilePath(naru.createRequest("4505"));
    }

    @Test
    void 요청을_보낼_수_있는_요소들을_세팅한다() {
        CrawlingResult crawlingResult = mock(CrawlingResult.class);
        when(jsoupCrawler.get(anyString())).thenReturn(crawlingResult);
        when(crawlingResult.getCookie(anyString())).thenReturn("MySessionId");
        when(crawlingResult.findElementAttribute(anyString(), anyString())).thenReturn("CSRF_TOKEN_VALUE");

        NaruDetailRequest request = naruCrawler.createRequest("4505");

        assertEquals("4505", request.getLibraryCode());
        assertEquals("MySessionId", request.getJSessionId());
        assertEquals("CSRF_TOKEN_VALUE", request.getCsrfToken());
    }

    @Test
    void 최근_도서정보_파일_경로를_가져올_수_있다() {
        CrawlingResult crawlingResult = mock(CrawlingResult.class);
        when(jsoupCrawler.post(any())).thenReturn(crawlingResult);
        when(crawlingResult.findElementText(anyString())).thenReturn("2024-11-01");
        when(crawlingResult.findElementAttribute(anyString(), anyString())).thenReturn("/MyFilePath");

        CsvFilePath result = naruCrawler.findCurrentBooksFilePath(
                new NaruDetailRequest("MySessionId", "MyCsrfToken", "123424"));

        assertThat(result.getPath()).contains("/MyFilePath");
        assertThat(result.getReferenceDate()).isEqualTo(LocalDate.parse("2024-11-01"));
    }

}