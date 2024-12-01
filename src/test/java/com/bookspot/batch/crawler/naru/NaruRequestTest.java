package com.bookspot.batch.crawler.naru;

import com.bookspot.batch.crawler.naru.exception.NaruRequestValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NaruRequestTest {
    @Test
    void 생성자_파라미터_중_하나라도_null이거나_빈_문자열_이면_예외_발생() {
        assertThrows(NaruRequestValidationException.class, () -> {
            new NaruRequest(null, "ABC", "DDD");
        });

        assertThrows(NaruRequestValidationException.class, () -> {
            new NaruRequest("", "ABC", "DDD");
        });
    }

}