package com.bookspot.batch.step.processor;

import com.bookspot.batch.data.LibraryStock;
import lombok.RequiredArgsConstructor;
import org.eclipse.collections.impl.set.mutable.primitive.LongHashSet;
import org.springframework.batch.item.ItemProcessor;

@RequiredArgsConstructor
public class ExistsStockFilter implements ItemProcessor<LibraryStock, LibraryStock> {
    private final LongHashSet longHashSet;

    @Override
    public LibraryStock process(LibraryStock item) throws Exception {
        if(longHashSet.contains(item.getBookId()))
            return null;
        return item;
    }
}
