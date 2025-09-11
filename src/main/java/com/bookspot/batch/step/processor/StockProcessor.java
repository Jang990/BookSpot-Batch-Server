package com.bookspot.batch.step.processor;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.csv.TextEllipsiser;
import com.bookspot.batch.step.processor.csv.book.SubjectCodeStringParser;
import com.bookspot.batch.step.service.memory.bookid.IsbnMemoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
@RequiredArgsConstructor
public class StockProcessor implements ItemProcessor<StockCsvData, LibraryStock> {
    private final TextEllipsiser textEllipsiser;
    private final SubjectCodeStringParser subjectCodeStringParser;
    private final IsbnMemoryRepository isbnMemoryRepository;
    private final long libraryId;

    @Override
    public LibraryStock process(StockCsvData item) throws Exception {
        Long bookId = isbnMemoryRepository.get(item.getIsbn());

        if (bookId == null) {
            return null;
        }

        String subjectCode = textEllipsiser.ellipsize(item.getSubjectCode(), 40);

        return new LibraryStock(
                libraryId,
                bookId,
                subjectCodeStringParser.parse(subjectCode)
        );
    }
}
