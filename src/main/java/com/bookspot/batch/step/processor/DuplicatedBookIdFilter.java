package com.bookspot.batch.step.processor;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.step.service.memory.isbn.BookIdSet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

@RequiredArgsConstructor
public class DuplicatedBookIdFilter implements ItemProcessor<LibraryStock, LibraryStock> {
    private final BookIdSet bookIdSet;

    @Override
    public LibraryStock process(LibraryStock item) throws Exception {
        if(bookIdSet.contains(item.getBookId()))
            return null;
        bookIdSet.add(item.getBookId());
        return item;
    }
}
