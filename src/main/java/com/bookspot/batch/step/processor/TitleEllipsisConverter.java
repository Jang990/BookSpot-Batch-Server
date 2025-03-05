package com.bookspot.batch.step.processor;

import com.bookspot.batch.data.file.csv.StockCsvData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TitleEllipsisConverter implements ItemProcessor<StockCsvData, StockCsvData> {
    private static final int MAX_TITLE_LENGTH = 200;
    public static final String ELLIPSIS = "...";

    @Override
    public StockCsvData process(StockCsvData item) throws Exception {
        String title = item.getTitle();
        if(title == null || title.length() <= MAX_TITLE_LENGTH)
            return item;

        return new StockCsvData(
                ellipsize(item.getTitle()),
                item.getAuthor(),
                item.getPublisher(),
                item.getPublicationYear(),
                item.getIsbn(),
                item.getVolume(),
                item.getSubjectCode(),
                item.getNumberOfBooks(),
                item.getLoanCount()
        );
    }

    private String ellipsize(String title) {
        return title.substring(0, textLength()).concat(ELLIPSIS);
    }

    private int textLength() {
        return MAX_TITLE_LENGTH - ELLIPSIS.length();
    }
}
