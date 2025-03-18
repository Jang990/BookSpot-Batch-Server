package com.bookspot.batch.global.crawler.kdc;

import org.springframework.stereotype.Service;

@Service
public class KdcTextParser {
    protected KdcCode parse(String text) {
        return new KdcCode(
                parseCode(text),
                parseName(text),
                parentCode(text)
        );
    }

    private Integer parentCode(String text) {
        int code = parseCode(text);
        if(code % 100 == 0)
            return null;
        if(code % 10 == 0)
            return code / 100 * 100;
        return code / 10 * 10;
    }

    private String parseName(String text) {
        if(text.startsWith("("))
            return text.substring(6).trim();
        return text.substring(4).trim();
    }

    private int parseCode(String text) {
        if(text.startsWith("("))
            return Integer.parseInt(text.substring(1, 4));

        return Integer.parseInt(text.substring(0, 3));
    }
}
