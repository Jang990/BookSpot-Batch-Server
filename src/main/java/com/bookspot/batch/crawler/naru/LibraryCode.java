package com.bookspot.batch.crawler.naru;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class LibraryCode {
    private final String code;

    public LibraryCode(String code) {
        this.code = code;
    }
}
