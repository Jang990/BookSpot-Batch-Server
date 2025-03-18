package com.bookspot.batch.global.crawler.kdc.css;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CssTargetTest {
    @Test
    void min부터_시작() {
        CssTarget target = new CssTarget(1, 3, 2);
        assertEquals(1, target.value());

        assertTrue(target.hasNext());
        target.next();
        assertEquals(3, target.value());
    }

    @Test
    void max까지만_next동작() {
        CssTarget target = new CssTarget(2, 3, 1);

        target.next();
        assertEquals(3, target.value());
        assertFalse(target.hasNext());

        assertThrows(OutOfRangeCssTargetException.class, target::next);
    }

    @Test
    void 값을_min으로_설정() {
        CssTarget target = new CssTarget(1, 100, 1);
        for (int i = 0; i < 50; i++)
            target.next();

        assertEquals(51, target.value());

        target.first();
        assertEquals(1, target.value());
    }

}