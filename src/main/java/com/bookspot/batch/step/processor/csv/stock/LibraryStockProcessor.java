package com.bookspot.batch.step.processor.csv.stock;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.csv.IsbnValidator;
import com.bookspot.batch.step.service.memory.bookid.IsbnMemoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
@RequiredArgsConstructor
public class LibraryStockProcessor implements ItemProcessor<StockCsvData, LibraryStock> {
    private final IsbnMemoryRepository isbnMemoryRepository;
    private final IsbnValidator isbnValidator;
    private final long libraryId;

    @Override
    public LibraryStock process(StockCsvData item) throws Exception {
        if (isbnValidator.isInValid(item.getIsbn())) {
            log.info("잘못된 ISBN13 -> {}", item);
            return null;
        }

        Long bookId = isbnMemoryRepository.get(item.getIsbn());

        if (bookId == null) {
            log.info("DB에서 찾을 수 없는 도서 정보 = {}", item);
            return null;
        }

        return new LibraryStock(libraryId, bookId);
    }
}
