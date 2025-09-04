package com.bookspot.batch.step.processor;

import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.csv.TextEllipsiser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TitleEllipsisConverter implements ItemProcessor<StockCsvData, StockCsvData> {
    public static final int MAX_TITLE_LENGTH = 200;
    private final TextEllipsiser textEllipsiser;

    @Override
    public StockCsvData process(StockCsvData item) throws Exception {
        String title = item.getTitle();
        if(title == null || title.length() <= MAX_TITLE_LENGTH)
            return item;

        return new StockCsvData(
                textEllipsiser.ellipsize(item.getTitle(), MAX_TITLE_LENGTH),
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
}
