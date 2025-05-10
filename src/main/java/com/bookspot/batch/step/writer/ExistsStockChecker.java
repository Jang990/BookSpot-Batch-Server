package com.bookspot.batch.step.writer;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.data.LibraryStockDto;
import com.bookspot.batch.global.file.stock.StockFilenameElement;
import com.bookspot.batch.step.service.memory.loan.LongLongPrimitiveConsumer;
import lombok.RequiredArgsConstructor;
import org.eclipse.collections.impl.map.mutable.primitive.LongBooleanHashMap;
import org.eclipse.collections.impl.set.mutable.primitive.LongHashSet;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;


@RequiredArgsConstructor
public class ExistsStockChecker implements ItemWriter<LibraryStock> {
    private final StockFilenameElement stockFilenameElement;
    private final LongBooleanHashMap bookIdExistsMap;

    public void processNonExistStock(LongLongPrimitiveConsumer consumer) {
        bookIdExistsMap.forEachKeyValue((bookId, isExist) -> {
            if(isExist)
                return;
            consumer.accept(bookId, stockFilenameElement.libraryId());
        });
    }

    @Override
    public void write(Chunk<? extends LibraryStock> chunk) throws Exception {
        for (LibraryStock libraryStock : chunk) {
            bookIdExistsMap.put(libraryStock.getBookId(), true);
        }
    }
}
