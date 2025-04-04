package com.bookspot.batch.step.listener;

import com.bookspot.batch.data.file.csv.StockCsvData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InvalidIsbn13LoggingListener implements ItemProcessListener<StockCsvData, Object> {
    @Override
    public void onProcessError(StockCsvData item, Exception e) {
        log.info("잘못된 ISBN Skip : {}", item);
    }
}
