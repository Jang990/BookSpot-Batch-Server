package com.bookspot.batch.step.processor.crawler;

import com.bookspot.batch.global.crawler.common.exception.ElementNotFoundException;
import com.bookspot.batch.global.crawler.naru.CsvFilePath;
import com.bookspot.batch.global.crawler.naru.NaruCrawler;
import com.bookspot.batch.global.crawler.naru.NaruDetailRequest;
import com.bookspot.batch.data.LibraryForFileParsing;
import com.bookspot.batch.data.crawler.StockFileData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockFilePathParser implements ItemProcessor<LibraryForFileParsing, StockFileData> {
    private final NaruCrawler naruCrawler;

    @Override
    public StockFileData process(LibraryForFileParsing item) {
        try {
            NaruDetailRequest request = naruCrawler.createRequest(item.naruDetail());
            CsvFilePath currentFilePath = naruCrawler.findCurrentBooksFilePath(request);

            if(item.stockUpdatedAt() != null
                    && !item.stockUpdatedAt().isBefore(currentFilePath.getReferenceDate()))
                return null;

            return new StockFileData(
                    item.libraryId(),
                    currentFilePath.getPath(),
                    currentFilePath.getReferenceDate());
        } catch (ElementNotFoundException e) {
            log.warn("도서관 CSV 파일을 찾을 수 없음. {}", item, e);
            return null;
        }
    }
}
