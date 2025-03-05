package com.bookspot.batch.step.processor;

import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InMemoryIsbnFilter implements ItemProcessor<StockCsvData, StockCsvData> {
    private final IsbnSet isbnSet;

    @Override
    public StockCsvData process(StockCsvData item) throws Exception {
        if (isbnSet.contains(item.getIsbn())) {
            log.trace("이미 존재하는 ISBN13 -> {}", item);
            return null;
        }
        return item;
    }
}
