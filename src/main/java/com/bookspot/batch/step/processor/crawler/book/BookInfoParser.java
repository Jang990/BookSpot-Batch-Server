package com.bookspot.batch.step.processor.crawler.book;

import com.bookspot.batch.data.Book;
import com.bookspot.batch.data.BookUniqueInfo;
import com.bookspot.batch.data.mapper.BookDataMapper;
import com.bookspot.batch.global.crawler.aladdin.AladdinCrawler;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookInfoParser implements ItemProcessor<BookUniqueInfo, Book> {
    private final AladdinCrawler aladdinCrawler;
    private final BookDataMapper dataMapper;

    @Override
    public Book process(BookUniqueInfo item) throws Exception {
        return dataMapper.transform(
                item.dbBookId(),
                aladdinCrawler.findBookDetail(item.isbn13()));
    }
}
