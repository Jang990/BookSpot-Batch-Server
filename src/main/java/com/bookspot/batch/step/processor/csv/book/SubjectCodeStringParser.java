package com.bookspot.batch.step.processor.csv.book;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SubjectCodeStringParser {
    public String parse(String subjectCode) {
        if (subjectCode == null) return null;

        long commaCount = subjectCode.chars().filter(c -> c == ',').count();
        long newlineCount = subjectCode.chars().filter(c -> c == '\n').count();

        if (commaCount + newlineCount > 1) {
            return null;
        }

        return subjectCode.replace(",", ".").replace("\n", ".");
    }
}
