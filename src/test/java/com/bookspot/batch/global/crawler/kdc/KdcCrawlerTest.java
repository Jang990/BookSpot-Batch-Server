package com.bookspot.batch.global.crawler.kdc;

import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KdcCrawlerTest {
    KdcCrawler crawler = new KdcCrawler(new JsoupCrawler(), new KdcTextParser());

    @Test
    void test() {
        crawler.findAll();
    }

}