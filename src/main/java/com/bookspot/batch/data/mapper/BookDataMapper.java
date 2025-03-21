package com.bookspot.batch.data.mapper;

import com.bookspot.batch.data.Book;
import com.bookspot.batch.global.crawler.aladdin.AladdinBookDetail;
import org.springframework.stereotype.Service;

@Service
public class BookDataMapper {
    public Book transform(long dbBookId, AladdinBookDetail aladdinBook) {
        return new Book(
                dbBookId,
                aladdinBook.getIsbn(),
                aladdinBook.getImage(),
                aladdinBook.getTitle(),
                aladdinBook.getSubTitle(),
                aladdinBook.getAuthor(),
                aladdinBook.getPublisher(),
                aladdinBook.getPublishedDate(),
                aladdinBook.getPageCount()
        );
    }
}
