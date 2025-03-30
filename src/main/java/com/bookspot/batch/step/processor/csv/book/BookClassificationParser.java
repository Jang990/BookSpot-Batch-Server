package com.bookspot.batch.step.processor.csv.book;

import org.springframework.stereotype.Service;

@Service
public class BookClassificationParser {
    private static final char[] PREFIX_DELIMITER = {'.', ','};
    private static final int MAX_PREFIX_LEN = 3;

    public Integer parsePrefix(String subjectCode) {
        if(subjectCode == null || subjectCode.isBlank())
            return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < subjectCode.length(); i++) {
            char c = subjectCode.charAt(i);

            if (isDigit(c)) {
                sb.append(c);
                continue;
            }

            if(isDelimiter(c))
                break;

            return null;
        }

        if (sb.length() <= 0 || MAX_PREFIX_LEN < sb.length()) {
            return null;
        }

        return Integer.parseInt(sb.toString());
    }

    private boolean isDelimiter(char c) {
        for (char delimiter : PREFIX_DELIMITER) {
            if(c == delimiter)
                return true;
        }
        return false;
    }

    private boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }
}
