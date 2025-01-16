package com.bookspot.batch.book.processor;

import com.bookspot.batch.book.data.Book;
import com.bookspot.batch.book.reader.BookCsvData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookCsvProcessor implements ItemProcessor<BookCsvData, Book> {
    private final YearParser yearParser;

    @Override
    public Book process(BookCsvData item) throws Exception {
        if(item.getIsbn13() == null
                || item.getIsbn13().isBlank()
                || item.getIsbn13().length() != 13)
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
