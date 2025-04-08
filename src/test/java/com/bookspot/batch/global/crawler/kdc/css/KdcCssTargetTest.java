package com.bookspot.batch.global.crawler.kdc.css;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class KdcCssTargetTest {

    @Test
    void leaf는_9를_초과할_수_없음() {
        KdcCssTarget target = new KdcCssTarget();

        next(8, v -> target.nextLeaf());

        assertEquals(9, target.leaf());
        assertThrows(OutOfRangeCssTargetException.class, () -> target.nextLeaf());
    }

    @Test
    void top은_39을_초과할_수_없음() {
        KdcCssTarget target = new KdcCssTarget();

        next(9, v -> target.nextTop());

        assertEquals(39, target.top());
        assertThrows(OutOfRangeCssTargetException.class, () -> target.nextTop());
    }

    @Test
    void mid와_midLine은_4_5를_초과할_수_없음() {
        KdcCssTarget target = new KdcCssTarget();

        next(9, v -> target.nextMid());

        assertMidValue(target, 4, 5);
        assertThrows(OutOfRangeCssTargetException.class, () -> target.nextMid());
    }

    @Test
    void leaf는_1_9까지_1씩_증가() {
        KdcCssTarget target = new KdcCssTarget();

        assertEquals(1, target.leaf());
        next(8, v -> target.nextLeaf());
        assertEquals(9, target.leaf());
    }

    @Test
    void midLine과_mid는_함께_증가() {
        KdcCssTarget target = new KdcCssTarget();

        next(4, v -> target.nextMid());
        assertMidValue(target, 2, 5);

        target.nextMid();
        assertMidValue(target, 4, 1);
    }

    @Test
    void leaf라인은_midLine보다_1큼() {
        KdcCssTarget target = new KdcCssTarget();

        assertEquals(2, target.midLine());
        assertEquals(3, target.leafLine());

        next(5, v -> target.nextMid());

        assertEquals(4, target.midLine());
        assertEquals(5, target.leafLine());
    }

    @Test
    void midLine과_mid는_최대_9번_증가() {
        KdcCssTarget target = new KdcCssTarget();

        assertMidValue(target, 2, 1);

        next(9, v -> target.nextMid());

        assertMidValue(target, 4, 5);
    }

    @Test
    void top은_12에서_39까지_3씩_증가() {
        KdcCssTarget target = new KdcCssTarget();
        assertEquals(target.top(), 12);

        target.nextTop();
        assertEquals(target.top(), 15);


        next(8, v -> target.nextTop());
        assertEquals(target.top(), 39);
    }

    private void next(int cnt, Consumer<Void> nextFunc) {
        for (int i = 0; i < cnt; i++)
            nextFunc.accept(null);
    }

    private static void assertMidValue(KdcCssTarget target, int midLine, int mid) {
        assertEquals(mid, target.mid());
        assertEquals(midLine, target.midLine());
    }
}