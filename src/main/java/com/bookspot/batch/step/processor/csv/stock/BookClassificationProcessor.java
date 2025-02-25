package com.bookspot.batch.step.processor.csv.stock;

import com.bookspot.batch.data.file.csv.LibraryStockCsvData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BookClassificationProcessor implements ItemProcessor<LibraryStockCsvData, LibraryStockCsvData> {
    private static final char[] delimiter = {'.', ','};
    private static final int MAX_PREFIX_LEN = 3;

    @Override
    public LibraryStockCsvData process(LibraryStockCsvData item) throws Exception {
        String subjectCode = item.getSubjectCode();

        return new LibraryStockCsvData(
                item.getIsbn(),
                getSubjectCodePrefix(subjectCode),
                item.getNumberOfBooks(),
                item.getLoanCount()
        );
    }

    private String getSubjectCodePrefix(String subjectCode) {
        if(hasPrefixNumber(subjectCode))
            return parsePrefix(subjectCode);
        return null;
    }

    private String parsePrefix(String subjectCode) {
        return subjectCode.substring(0, MAX_PREFIX_LEN);
    }

    private boolean hasPrefixNumber(String subjectCode) {
        if(subjectCode == null || subjectCode.isBlank())
            return false;

        return parsePrefix(subjectCode).length() <= MAX_PREFIX_LEN;
    }
}
