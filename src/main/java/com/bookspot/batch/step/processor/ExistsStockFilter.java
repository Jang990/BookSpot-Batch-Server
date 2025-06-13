package com.bookspot.batch.step.processor;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.step.service.memory.isbn.BookIdSet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

@RequiredArgsConstructor
public class ExistsStockFilter implements ItemProcessor<LibraryStock, LibraryStock> {
    private final BookIdSet libraryBookIdSet;

    @Override
    public LibraryStock process(LibraryStock item) throws Exception {
        if(libraryBookIdSet.contains(item.getBookId()))
            return null;
        return item;
    }
}
