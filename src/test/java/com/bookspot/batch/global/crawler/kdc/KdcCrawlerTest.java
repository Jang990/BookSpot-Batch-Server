package com.bookspot.batch.global.crawler.kdc;

import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KdcCrawlerTest {
    KdcCrawler crawler = new KdcCrawler(new JsoupCrawler(), new KdcCodeParser(new KdcTextParser()));

    @Test
    void test() {
        List<KdcCode> result = crawler.findAll();

        assertEquals(910, result.size());

        assertEquals(new KdcCode(0, "총류", null), result.get(0));
        assertEquals(new KdcCode(100, "철학", null), result.get(1));
        assertEquals(new KdcCode(200, "종교", null), result.get(2));
        assertEquals(new KdcCode(300, "사회과학", null), result.get(3));
        assertEquals(new KdcCode(400, "자연과학", null), result.get(4));
        assertEquals(new KdcCode(500, "기술과학", null), result.get(5));
        assertEquals(new KdcCode(600, "예술", null), result.get(6));
        assertEquals(new KdcCode(700, "언어", null), result.get(7));
        assertEquals(new KdcCode(800, "문학", null), result.get(8));
        assertEquals(new KdcCode(900, "역사", null), result.get(9));
        assertEquals(new KdcCode(10, "도서학, 서지학", 0), result.get(10));
        assertEquals(new KdcCode(20, "문헌정보학", 0), result.get(11));

        assertEquals(new KdcCode(996, "오세아니아와 양극 전기", 990), result.get(907));
        assertEquals(new KdcCode(998, "주제별 전기", 990), result.get(908));
        assertEquals(new KdcCode(999, "계보, 족보", 990), result.get(909));
    }

}