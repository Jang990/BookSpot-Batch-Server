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
        System.out.println(result);

        assertEquals(new KdcCode(0, "총류", null), result.getFirst());
        assertEquals(new KdcCode(1, "지식 및 학문 일반", 0), result.get(1));
        assertEquals(new KdcCode(3, "이론 체계 및 시스템", 0), result.get(2));
        assertEquals(new KdcCode(4, "컴퓨터과학", 0), result.get(3));
        assertEquals(new KdcCode(5, "프로그래밍, 프로그램, 데이터", 0), result.get(4));

        assertEquals(new KdcCode(990, "전기", 900), result.get(901));
        assertEquals(new KdcCode(996, "오세아니아와 양극 전기", 990), result.get(907));
        assertEquals(new KdcCode(998, "주제별 전기", 990), result.get(908));
        assertEquals(new KdcCode(999, "계보, 족보", 990), result.get(909));
    }

}