package com.bookspot.batch.data.mapper;

import com.bookspot.batch.data.BookDocument;
import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import org.springframework.stereotype.Service;

@Service
public class BookToDocumentMapper {
    public BookDocument transform(ConvertedUniqueBook book) {
        return new BookDocument(
                book.getId(),
                book.getIsbn13(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getLoanCount(),
                book.getSubjectCode(),
                book.getPublicationYear() == null ? null : book.getPublicationYear().getValue()
        );
    }
}
