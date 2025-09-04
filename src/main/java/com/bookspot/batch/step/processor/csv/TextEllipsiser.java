package com.bookspot.batch.step.processor.csv;

import org.springframework.stereotype.Component;

@Component
public class TextEllipsiser {
    public static final String ELLIPSIS = "...";

    public String ellipsize(String text, int maxLen) {
        if(text == null || text.length() <= maxLen)
            return text;
        return text.substring(0, textLength(maxLen)).concat(ELLIPSIS);
    }

    private int textLength(int maxLen) {
        return maxLen - ELLIPSIS.length();
    }
}
