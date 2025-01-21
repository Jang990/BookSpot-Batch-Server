package com.bookspot.batch.library.naru;

import com.bookspot.batch.global.crawler.common.CrawlingResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LibraryListPageConvertorTest {

    LibraryListPageConvertor convertor = new LibraryListPageConvertor();

    @Test
    void 도서관_상세페이지_번호_파싱하기() {
        CrawlingResult crawling = mock(CrawlingResult.class);
        when(crawling.findElementAttribute(any(), any()))
                .thenReturn("detailView('8517');");

        assertEquals("8517", convertor.findDetailNumber(crawling, 1));
    }

    @Test
    void 도서관_이름_불러오기() {
        CrawlingResult crawling = mock(CrawlingResult.class);
        when(crawling.findElementText(any())).thenReturn("ABC 도서관");

        assertEquals("ABC 도서관", convertor.findLibraryName(crawling, 1));
    }

    @Test
    void 도서관_주소_불러오기() {
        CrawlingResult crawling = mock(CrawlingResult.class);
        when(crawling.findElementText(any())).thenReturn("ABC대로 11-22");

        assertEquals("ABC대로 11-22", convertor.findAddress(crawling, 1));
    }
}