package com.bookspot.batch.book.processor;

import com.bookspot.batch.book.data.Book;
import com.bookspot.batch.book.reader.BookCsvData;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class BookCsvProcessor implements ItemProcessor<BookCsvData, Book> {
    @Override
    public Book process(BookCsvData item) throws Exception {
        if(item.getIsbn13() == null)
            return null;

        return new Book(
                item.getIsbn13(),
                item.getTitle(),
                item.getAuthor(),
                item.getPublicationYear(),
                item.getSubjectCode()
        );
    }
}
