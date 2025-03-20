package com.bookspot.batch.global.crawler.kdc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class KdcTextParserTest {
    KdcTextParser parser = new KdcTextParser(new KdcParentBookCodeResolver());

    @ParameterizedTest
    @MethodSource("args")
    void kdc문서의_Text를_KdcCode로변환(String original, KdcCode expected) {
        assertEquals(expected, parser.parse(original));
    }


    static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(
                        "800 문학",
                        new KdcCode(800, "문학", null)
                ),
                Arguments.of(
                        "120 인식론, 인과론, 인간학",
                        new KdcCode(120, "인식론, 인과론, 인간학", 100)
                ),
                Arguments.of(
                        "(849) 미국문학",
                        new KdcCode(849, "미국문학", 840)
                )
        );
    }
}