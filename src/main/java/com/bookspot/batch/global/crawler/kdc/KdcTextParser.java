package com.bookspot.batch.global.crawler.kdc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KdcTextParser {
    private final KdcParentBookCodeResolver parentCodeResolver;

    protected KdcCode parse(String text) {
        int code = parseCode(text);
        return new KdcCode(
                code, parseName(text),
                parentCodeResolver.resolve(code)
        );
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
