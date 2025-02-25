package com.bookspot.batch.step.processor.csv.stock;

import com.bookspot.batch.data.file.csv.ConvertedStockCsvData;
import com.bookspot.batch.data.file.csv.LibraryStockCsvData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BookClassificationProcessor implements ItemProcessor<LibraryStockCsvData, ConvertedStockCsvData> {
    private static final char[] PREFIX_DELIMITER = {'.', ','};
    private static final int MAX_PREFIX_LEN = 3;

    @Override
    public ConvertedStockCsvData process(LibraryStockCsvData item) throws Exception {
        String subjectCode = item.getSubjectCode();

        return new ConvertedStockCsvData(
                item.getIsbn(),
                parsePrefix(subjectCode),
                item.getNumberOfBooks(),
                item.getLoanCount()
        );
    }

    private Integer parsePrefix(String subjectCode) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < subjectCode.length(); i++) {
            char c = subjectCode.charAt(i);

            if (isDigit(c)) {
                sb.append(c);
                continue;
            }

            if(isDelimiter(c))
                break;

            log.trace("숫자와 구분자가 아닌 문자가 포함된 분류번호 : {}", subjectCode);
            return null;
        }

        if (sb.length() > MAX_PREFIX_LEN) {
            log.trace("너무 긴 도서 분류 번호 : {}", subjectCode);
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
