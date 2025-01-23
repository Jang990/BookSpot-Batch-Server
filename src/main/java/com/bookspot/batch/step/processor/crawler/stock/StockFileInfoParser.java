package com.bookspot.batch.step.processor.crawler.stock;

import com.bookspot.batch.global.crawler.common.exception.ElementNotFoundException;
import com.bookspot.batch.global.crawler.naru.CsvFilePath;
import com.bookspot.batch.global.crawler.naru.NaruCrawler;
import com.bookspot.batch.global.crawler.naru.NaruDetailRequest;
import com.bookspot.batch.stock.data.CurrentLibrary;
import com.bookspot.batch.stock.data.StockFileData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockFileInfoParser implements ItemProcessor<CurrentLibrary, StockFileData> {
    private final NaruCrawler naruCrawler;

    @Override
    public StockFileData process(CurrentLibrary item) {
        try {
            NaruDetailRequest request = naruCrawler.createRequest(item.naruDetail());
            CsvFilePath currentFilePath = naruCrawler.findCurrentBooksFilePath(request);

            if(item.stockUpdatedAt() != null
                    && !item.stockUpdatedAt().isBefore(currentFilePath.getReferenceDate()))
                return null;

            return new StockFileData(
                    item.libraryCode(),
                    currentFilePath.getPath(),
                    currentFilePath.getReferenceDate());
        } catch (ElementNotFoundException e) {
            log.warn("도서관 CSV 파일을 찾을 수 없음. {}", item, e);
            return null;
        }
    }
}
