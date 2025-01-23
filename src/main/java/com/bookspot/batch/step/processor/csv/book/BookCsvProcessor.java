package com.bookspot.batch.step.processor.csv.book;

import com.bookspot.batch.book.data.Book;
import com.bookspot.batch.step.reader.file.csv.book.BookCsvData;
import com.bookspot.batch.step.processor.csv.IsbnValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookCsvProcessor implements ItemProcessor<BookCsvData, Book> {
    private final YearParser yearParser;
    private final IsbnValidator isbnValidator;

    @Override
    public Book process(BookCsvData item) throws Exception {
        if(isbnValidator.isInValid(item.getIsbn13()))
            return null;

        return new Book(
                item.getIsbn13(),
                item.getTitle(),
                item.getAuthor(),
                yearParser.parse(item.getPublicationYear()),
                item.getSubjectCode(),
                item.getVolume()
        );
    }
}
