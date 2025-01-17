package com.bookspot.batch.stock.processor;

import com.bookspot.batch.book.BookRepository;
import com.bookspot.batch.library.LibraryRepository;
import com.bookspot.batch.stock.data.LibraryStock;
import com.bookspot.batch.stock.data.LibraryStockCsvData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryStockProcessor implements ItemProcessor<LibraryStockCsvData, LibraryStock> {
    private final BookRepository bookRepository;
    private final LibraryRepository libraryRepository;

    @Override
    public LibraryStock process(LibraryStockCsvData item) throws Exception {
        //TODO: "128056"로 하드코딩된 도서관 코드를 변경해야한다. ExecutionContext로?
        Optional<Long> libraryId = libraryRepository.findId("128056");
        Optional<Long> bookId = bookRepository.findIdByIsbn13(item.getIsbn());

        if (libraryId.isEmpty() || bookId.isEmpty()) {
            log.info("DB에서 찾을 수 없는 도서 또는 도서관 정보 = {}", item);
            return null;
        }

        return new LibraryStock(
                libraryId.get(),
                bookId.get()
        );
    }
}
