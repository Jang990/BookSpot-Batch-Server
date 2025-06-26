package com.bookspot.batch.step.listener;

import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.exception.InvalidIsbn13Exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InvalidIsbn13LoggingListener implements ItemProcessListener<StockCsvData, Object> {
    @Override
    public void onProcessError(StockCsvData item, Exception e) {
        if(e instanceof InvalidIsbn13Exception iie)
            log.info("잘못된 ISBN Skip : LibraryId: {}, BookDetail: {}", iie.getLibraryId(),item);
    }
}
