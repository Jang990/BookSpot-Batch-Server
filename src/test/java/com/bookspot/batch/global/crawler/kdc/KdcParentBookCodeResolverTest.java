package com.bookspot.batch.global.crawler.kdc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class KdcParentBookCodeResolverTest {
    KdcParentBookCodeResolver resolver = new KdcParentBookCodeResolver();

    @ParameterizedTest
    @MethodSource("args")
    void kdc_규칙에_맞게_code를_parentCode로_변환해준다(Integer expected, int code) {
        assertEquals(expected, resolver.resolve(code));
    }

    static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(null, 0),
                Arguments.of(null, 100),
                Arguments.of(0, 10),
                Arguments.of(10, 15),
                Arguments.of(100, 110),
                Arguments.of(110, 115),
                Arguments.of(600, 640),
                Arguments.of(810, 812)
        );
    }

}