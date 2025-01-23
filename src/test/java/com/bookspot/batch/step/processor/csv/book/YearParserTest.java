package com.bookspot.batch.step.processor.csv.book;

import com.bookspot.batch.step.processor.csv.book.YearParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YearParserTest {

    YearParser yearParser = new YearParser();

    @Test
    void _4글자로된_연속된_숫자만_파싱함() {
        assertEquals(2024, yearParser.parse("2024"));
        assertEquals(2021, yearParser.parse("c2021"));
        assertEquals(2023, yearParser.parse("2023c"));
        assertEquals(2002, yearParser.parse("2002'"));
        assertEquals(1999, yearParser.parse("'1999"));
        assertEquals(2023, yearParser.parse("ak2023,"));
    }

    @Test
    void _4글자의_연속된_숫자가_아니라면_null_반환() {
        assertNull(yearParser.parse(null));
        assertNull(yearParser.parse("abc2"));
        assertNull(yearParser.parse("123"));
        assertNull(yearParser.parse("12a35"));
        assertNull(yearParser.parse("20241272918"));
    }

}