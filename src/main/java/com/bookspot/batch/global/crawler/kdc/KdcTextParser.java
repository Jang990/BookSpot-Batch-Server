package com.bookspot.batch.global.crawler.kdc;

import org.springframework.stereotype.Service;

import javax.annotation.Nullable;

@Service
public class KdcTextParser {
    protected KdcCode parse(String text, @Nullable KdcCode parentCode) {
        return new KdcCode(
                Integer.parseInt(text.substring(0, 3)),
                text.substring(4),
                parentCode == null ? null : parentCode.code()
        );
    }
}
