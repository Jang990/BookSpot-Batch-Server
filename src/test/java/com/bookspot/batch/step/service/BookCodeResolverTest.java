package com.bookspot.batch.step.service;

import com.bookspot.batch.data.BookCategories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class BookCodeResolverTest {
    private static Map<Integer, String> sampleMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "총류");
            map.put(1, "지식 및 학문 일반");
            map.put(10, "도서학, 서지학");
                map.put(19, "장서목록");
            map.put(30, "백과사전");
                map.put(32, "중국어");
                map.put(33, "일본어");
        map.put(100, "철학");
            map.put(102, "잡저");
            map.put(110, "형이상학");
                map.put(111, "방법론");
                map.put(112, "존재론");
            map.put(120, "인식론, 인과론, 인간학");
                map.put(123, "자유 및 필연");
                map.put(124, "목적론");

        return map;
    }

    BookCodeResolver resolver = new BookCodeResolver(sampleMap());

    @Test
    void 분류코드는_0_999사이_숫자() {
        assertThrows(IllegalArgumentException.class, () -> resolver.resolve(-1));
        assertThrows(IllegalArgumentException.class, () -> resolver.resolve(1000));
        resolver.resolve(0);
        resolver.resolve(500);
        resolver.resolve(999);
    }

    @ParameterizedTest
    @MethodSource("args")
    void 분류코드를_풀어줌(int bookCode, BookCategories expected) {
        assertThat(resolver.resolve(bookCode))
                .isEqualTo(expected);
    }

    static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(0, BookCategories.topCategory("000.총류")),
                Arguments.of(1, BookCategories.leafCategory("000.총류", "000.총류", "001.지식 및 학문 일반")),
                Arguments.of(19, BookCategories.leafCategory("000.총류", "010.도서학, 서지학", "019.장서목록")),
                Arguments.of(32, BookCategories.leafCategory("000.총류", "030.백과사전", "032.중국어")),
                Arguments.of(33, BookCategories.leafCategory("000.총류", "030.백과사전", "033.일본어")),
                Arguments.of(100, BookCategories.topCategory("100.철학")),
                Arguments.of(102, BookCategories.leafCategory("100.철학", "100.철학","102.잡저")),
                Arguments.of(110, BookCategories.midCategory("100.철학","110.형이상학")),
                Arguments.of(112, BookCategories.leafCategory("100.철학","110.형이상학","112.존재론")),
                Arguments.of(120, BookCategories.midCategory("100.철학","120.인식론, 인과론, 인간학")),
                Arguments.of(124, BookCategories.leafCategory("100.철학","120.인식론, 인과론, 인간학", "124.목적론")),
                Arguments.of(125, BookCategories.midCategory("100.철학","120.인식론, 인과론, 인간학" /* 존재하지 않음 */))
        );
    }

}