package com.bookspot.batch.step.processor.csv.stock;

import com.bookspot.batch.step.processor.csv.stock.repository.BookRepository;
import com.bookspot.batch.step.processor.csv.stock.repository.LibraryRepository;
import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.data.file.csv.LibraryStockCsvData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class LibraryStockProcessor implements ItemProcessor<LibraryStockCsvData, LibraryStock> {
    private final BookRepository bookRepository;
    private final long libraryId;

    @Override
    public LibraryStock process(LibraryStockCsvData item) throws Exception {
        Optional<Long> bookId = bookRepository.findIdByIsbn13(item.getIsbn());

        if (bookId.isEmpty()) {
            log.info("DB에서 찾을 수 없는 도서 정보 = {}", item);
            return null;
        }

        return new LibraryStock(
                libraryId,
                bookId.get()
        );
    }
}
