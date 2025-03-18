package com.bookspot.batch.global.crawler.kdc;

import org.springframework.stereotype.Service;

import javax.annotation.Nullable;

@Service
public class KdcTextParser {
    protected KdcCode parse(String text, @Nullable KdcCode parentCode) {
        return new KdcCode(
                parseCode(text),
                parseName(text),
                parentCode == null ? null : parentCode.code()
        );
    }

    private static String parseName(String text) {
        if(text.startsWith("("))
            return text.substring(6).trim();
        return text.substring(4).trim();
    }

    private static int parseCode(String text) {
        if(text.startsWith("("))
            return Integer.parseInt(text.substring(1, 4));

        return Integer.parseInt(text.substring(0, 3));
    }
}
