package com.bookspot.batch.global.crawler.aladdin.detail;

import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AladdinDetailLinkFinderTest {
    AladdinDetailLinkFinder finder = new AladdinDetailLinkFinder(new JsoupCrawler());

    @Test
    void 단일_검색결과가_아니라면_예외발생() {
        assertThrows(IllegalArgumentException.class, () -> finder.find("객체"));
    }

    @Test
    void 단일_결과가_나오는_ISBN을_주면_책_세부_페이지_링크를_알_수_있다() {
        assertEquals("https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=60550259",
                finder.find("9788998139766"));
    }

}