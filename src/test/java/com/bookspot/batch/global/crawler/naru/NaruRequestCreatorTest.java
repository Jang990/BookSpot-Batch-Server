package com.bookspot.batch.global.crawler.naru;

import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

class NaruRequestCreatorTest {

    NaruRequestCreator creator = new NaruRequestCreator(new JsoupCrawler());

//    @Test
    void test() {
        NaruCommonRequest request = creator.create();
        assertNotNull(request.getCsrfToken());
        assertNotNull(request.getJSessionId());
        assertFalse(request.getCsrfToken().isBlank());
        assertFalse(request.getJSessionId().isBlank());
    }

}