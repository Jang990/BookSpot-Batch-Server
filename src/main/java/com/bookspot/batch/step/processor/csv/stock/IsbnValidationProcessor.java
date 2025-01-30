package com.bookspot.batch.step.processor.csv.stock;

import com.bookspot.batch.step.processor.csv.IsbnValidator;
import com.bookspot.batch.data.file.csv.LibraryStockCsvData;
import com.bookspot.batch.step.service.IsbnMemoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IsbnValidationProcessor implements ItemProcessor<LibraryStockCsvData, LibraryStockCsvData> {
    private final IsbnValidator validator;
    private final IsbnMemoryRepository isbnMemoryRepository;
    @Override
    public LibraryStockCsvData process(LibraryStockCsvData item) throws Exception {
        if (validator.isInValid(item.getIsbn())) {
            log.info("잘못된 ISBN13 -> {}", item);
            return null;
        }

        if (isbnMemoryRepository.contains(item.getIsbn())) {
            log.trace("이미 존재하는 ISBN13 -> {}", item);
            return null;
        }
        return item;
    }
}
