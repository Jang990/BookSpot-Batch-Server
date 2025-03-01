package com.bookspot.batch.step.processor;

import com.bookspot.batch.step.processor.csv.IsbnValidator;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.service.memory.bookid.IsbnMemoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IsbnValidationFilter implements ItemProcessor<StockCsvData, StockCsvData> {
    private final IsbnValidator isbnValidator;
    private final IsbnMemoryRepository isbnMemoryRepository;

    @Override
    public StockCsvData process(StockCsvData item) throws Exception {
        if (isbnValidator.isInValid(item.getIsbn())) {
            log.info("잘못된 ISBN13 -> {}", item);
            return null;
        }

        /*if (!isbnValidator.isBookType(item.getIsbn())) {
            log.info("책 이외의 자료 ISBN -> {}", item);
            return null;
        }*/

        if (isbnMemoryRepository.contains(item.getIsbn())) {
            log.trace("이미 존재하는 ISBN13 -> {}", item);
            return null;
        }

        return item;
    }
}
