package com.bookspot.batch.step.processor.crawler.book;

import com.bookspot.batch.data.Book;
import com.bookspot.batch.global.crawler.aladdin.AladdinCrawler;
import com.bookspot.batch.step.service.memory.bookid.Isbn13MemoryData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookInfoParser implements ItemProcessor<Isbn13MemoryData, Book> {
    private final AladdinCrawler aladdinCrawler;
    private final BookDataMapper dataMapper;

    @Override
    public Book process(Isbn13MemoryData item) throws Exception {
        return dataMapper.transform(
                item.bookId(),
                aladdinCrawler.findBookDetail(item.isbn13()));
    }
}
