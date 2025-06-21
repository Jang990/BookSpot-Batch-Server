package com.bookspot.batch.step.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class BookCodeResolver {
    private final Map<Integer, String> bookCodeMap;
    private static final int CODE_MIN = 0, CODE_MAX = 999;
    private static final String FORMAT = "%03d.%s";
    private static final String EMPTY = "EMPTY";

    public List<String> resolve(final int bookCode) {
        if(bookCode < CODE_MIN || CODE_MAX < bookCode)
            throw new IllegalArgumentException("도서의 분류 코드는 0~999사이");

        int top    = (bookCode / 100) * 100;
        int middle = (bookCode / 10)  * 10;
        int leaf = bookCode;
        return Stream.of(top, middle, leaf)
                .distinct()
                .map(this::format)
                .filter(s -> {
                    boolean isValidCode = !s.equals(EMPTY);
                    if(!isValidCode)
                        log.trace("존재하지 않는 책 코드 : {}", bookCode);
                    return isValidCode;
                })
                .toList();
    }

    private String format(int bookCode) {
        String codeName = bookCodeMap.get(bookCode);
        if (codeName == null) {
            return EMPTY;
        }
        return FORMAT.formatted(bookCode, codeName);
    }
}
