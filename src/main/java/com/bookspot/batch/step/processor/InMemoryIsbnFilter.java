package com.bookspot.batch.step.processor;

import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

@RequiredArgsConstructor
public class InMemoryIsbnFilter implements ItemProcessor<StockCsvData, StockCsvData> {
    private final IsbnSet isbnSet;

    @Override
    public StockCsvData process(StockCsvData item) throws Exception {
        if(isbnSet.contains(item.getIsbn()))
            return null;
        return item;
    }
}
