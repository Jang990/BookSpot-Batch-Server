package com.bookspot.batch.global.crawler.naru;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

class NaruPagingUrlBuilderTest {

    NaruPagingUrlBuilder builder = new NaruPagingUrlBuilder();

    @Test
    void 정보나루_페이징_url_완성() {
        assertEquals(
                "naru?pageNo=2&pageSize=10",
                builder.build("naru", PageRequest.of(2, 10)));

    }

}