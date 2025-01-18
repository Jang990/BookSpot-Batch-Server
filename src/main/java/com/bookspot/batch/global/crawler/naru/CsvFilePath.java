package com.bookspot.batch.global.crawler.naru;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@ToString
public class CsvFilePath {
    private final String path;
    private final LocalDate referenceDate;
}
