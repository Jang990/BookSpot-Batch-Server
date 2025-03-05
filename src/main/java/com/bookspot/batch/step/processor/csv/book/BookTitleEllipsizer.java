package com.bookspot.batch.step.processor.csv.book;

import org.springframework.stereotype.Service;

@Service
public class BookTitleEllipsizer {
    public static final String ELLIPSIS = "...";

    public String ellipsize(String title) {
        if(title == null || title.length() <= ELLIPSIS.length())
            return title;
        return title.substring(0, title.length() - ELLIPSIS.length())
                .concat(ELLIPSIS);
    }
}
