package com.bookspot.batch.step.processor;

import com.bookspot.batch.global.file.stock.StockFilenameUtil;
import com.bookspot.batch.step.processor.csv.IsbnValidator;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.exception.InvalidIsbn13Exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.core.io.Resource;

@Slf4j
public class IsbnValidationFilter implements ItemProcessor<StockCsvData, StockCsvData> {
    private final IsbnValidator isbnValidator;
    private final long libraryId;

    public IsbnValidationFilter(IsbnValidator isbnValidator, long libraryId) {
        this.isbnValidator = isbnValidator;
        this.libraryId = libraryId;
    }

    public IsbnValidationFilter(IsbnValidator isbnValidator, Resource file) {
        this.isbnValidator = isbnValidator;
        this.libraryId = StockFilenameUtil.parse(file.getFilename()).libraryId();
    }

    @Override
    public StockCsvData process(StockCsvData item) throws Exception {
        if (isbnValidator.isInValid(item.getIsbn())) {
            throw new InvalidIsbn13Exception(libraryId);
        }

        /*if (!isbnValidator.isBookType(item.getIsbn())) {
            log.info("책 이외의 자료 ISBN -> {}", item);
            return null;
        }*/

        return item;
    }
}
