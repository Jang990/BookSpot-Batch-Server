package com.bookspot.batch.step.processor.csv.book;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTitleEllipsizerTest {

    BookTitleEllipsizer ellipsizer = new BookTitleEllipsizer();

    @Test
    void 생략부호보다_작다면_그대로_반환함() {
        assertEquals("ABC", ellipsizer.ellipsize("ABC"));
    }

    @Test
    void 생략부호보다_크다면_제목의_끝에_문자들을_생략부호로_변환해줌() {
        assertEquals("A...", ellipsizer.ellipsize("ABCD"));
        assertEquals("ABCABC...", ellipsizer.ellipsize("ABCABCABC"));
    }

}