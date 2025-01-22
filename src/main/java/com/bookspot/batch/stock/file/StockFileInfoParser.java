package com.bookspot.batch.stock.file;

import com.bookspot.batch.global.crawler.common.exception.ElementNotFoundException;
import com.bookspot.batch.global.crawler.naru.CsvFilePath;
import com.bookspot.batch.global.crawler.naru.NaruCrawler;
import com.bookspot.batch.global.crawler.naru.NaruDetailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockFileInfoParser {
    private final NaruCrawler naruCrawler;

    public StockFileData parse(CurrentLibrary library) {
        try {
            NaruDetailRequest request = naruCrawler.createRequest(library.libraryCode());
            CsvFilePath currentFilePath = naruCrawler.findCurrentBooksFilePath(request);

            if(library.stockUpdatedAt() != null
                    && !library.stockUpdatedAt().isBefore(currentFilePath.getReferenceDate()))
                return null;

            return new StockFileData(
                    library.libraryCode(),
                    currentFilePath.getPath(),
                    currentFilePath.getReferenceDate());
        } catch (ElementNotFoundException e) {
            log.warn("도서관 CSV 파일을 찾을 수 없음.", e);
            return null;
        }
    }

}
