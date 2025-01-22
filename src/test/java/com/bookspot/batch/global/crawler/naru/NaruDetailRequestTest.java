package com.bookspot.batch.global.crawler.naru;

import com.bookspot.batch.global.crawler.naru.exception.NaruRequestValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NaruDetailRequestTest {
    @Test
    void 생성자_파라미터_중_하나라도_null이거나_빈_문자열_이면_예외_발생() {
        assertThrows(NaruRequestValidationException.class, () -> {
            new NaruDetailRequest(null, "ABC", "DDD");
        });

        assertThrows(NaruRequestValidationException.class, () -> {
            new NaruDetailRequest("", "ABC", "DDD");
        });
    }

    @Test
    void body로_보낼_String_을_만들_수_있다() {
        NaruDetailRequest request = new NaruDetailRequest("F47524315599BE53C105E6CD4525794F", "ae97f8b9-b088-4469-be8c-70655731c8c8", "10801");
        assertEquals("_csrf=ae97f8b9-b088-4469-be8c-70655731c8c8&libcode=10801", request.getRequestBody());
    }

}