package com.bookspot.batch.step.processor.csv.book;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubjectCodeStringParserTest {
    SubjectCodeStringParser parser = new SubjectCodeStringParser();

    @Test
    void 정상처리() {
        assertEquals("123.456",parser.parse("123.456"));
        assertEquals("123.456",parser.parse("123,456"));
        assertEquals("123.456",parser.parse("123\n456"));
    }

    @Test
    void null_처리() {
        assertNull(parser.parse("1,2,3"));
        assertNull(parser.parse("1\n2\n3"));
        assertNull(parser.parse("1,2\n3"));
    }
}