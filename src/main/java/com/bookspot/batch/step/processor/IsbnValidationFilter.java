package com.bookspot.batch.step.processor;

import com.bookspot.batch.step.processor.csv.IsbnValidator;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.exception.InvalidIsbn13Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IsbnValidationFilter implements ItemProcessor<StockCsvData, StockCsvData> {
    private final IsbnValidator isbnValidator;

    @Override
    public StockCsvData process(StockCsvData item) throws Exception {
        if (isbnValidator.isInValid(item.getIsbn())) {
            throw new InvalidIsbn13Exception();
        }

        /*if (!isbnValidator.isBookType(item.getIsbn())) {
            log.info("책 이외의 자료 ISBN -> {}", item);
            return null;
        }*/

        return item;
    }
}
