package com.bookspot.batch.library.reader.crawler;

import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import com.bookspot.batch.global.crawler.naru.NaruPagingUrlBuilder;
import com.bookspot.batch.library.data.LibraryNaruDetail;
import com.bookspot.batch.library.reader.crawler.LibraryListPageConvertor;
import com.bookspot.batch.library.reader.crawler.NaruDetailParser;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NaruDetailParserTest {
    NaruDetailParser parser = new NaruDetailParser(
            new JsoupCrawler(),
            new NaruPagingUrlBuilder(),
            new LibraryListPageConvertor());

//    @Test
    void test() {
        List<LibraryNaruDetail> result = parser.parseDetail(PageRequest.of(1, 1));

        assertEquals(1, result.size());
        assertEquals("2.28도서관", result.get(0).name());
        assertEquals("대구광역시 중구 2·28길 9", result.get(0).address());
        assertEquals("29981", result.get(0).naruDetail());
    }

}