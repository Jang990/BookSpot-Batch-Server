package com.bookspot.batch.book.processor;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class YearParser {
    private static final Pattern YEAR_PATTERN = Pattern.compile("\\d{4,}");

    public Integer parse(String year) {
        if(year == null)
            return null;

        Matcher matcher = YEAR_PATTERN.matcher(year);
        if(!matcher.find())
            return null;
        return toInteger(findYear(matcher.group()));
    }

    private Integer toInteger(String year) {
        if(year == null)
            return null;
        return Integer.parseInt(year);
    }

    private String findYear(String year) {
        if(year.length() == 4)
            return year;
        return null;
    }
}
