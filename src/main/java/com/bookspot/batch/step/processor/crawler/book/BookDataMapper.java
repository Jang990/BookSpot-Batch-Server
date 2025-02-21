package com.bookspot.batch.step.processor.crawler.book;

import com.bookspot.batch.data.Book_;
import com.bookspot.batch.global.crawler.aladdin.AladdinBookDetail;
import org.springframework.stereotype.Service;

@Service
public class BookDataMapper {
    public Book_ transform(long dbBookId, AladdinBookDetail aladdinBook) {
        return new Book_(
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
