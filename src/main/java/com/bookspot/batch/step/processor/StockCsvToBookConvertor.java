package com.bookspot.batch.step.processor;

import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.csv.book.BookClassificationParser;
import com.bookspot.batch.step.processor.csv.book.YearParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.Year;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockCsvToBookConvertor implements ItemProcessor<StockCsvData, ConvertedUniqueBook> {
    private final BookClassificationParser classificationParser;
    private final YearParser yearParser;

    @Override
    public ConvertedUniqueBook process(StockCsvData item) throws Exception {
        return new ConvertedUniqueBook(
                item.getIsbn(),
                item.getTitle(),
                item.getAuthor(),
                item.getPublisher(),
                item.getVolume(),
                item.getNumberOfBooks(),
                item.getLoanCount(),
                parseClassificationPrefix(item),
                parseYear(item)
        );
    }

    private Year parseYear(StockCsvData item) {
        Integer yearNum = yearParser.parse(item.getPublicationYear());
        if (yearNum == null) {
            log.trace("파싱할 수 없는 연도 String : {}", item);
            return null;
        }

        try  {
            return Year.of(yearNum);
        } catch (DateTimeException e) {
            log.trace("잘못된 범위의 연도 String : {}", item);
            return null;
        }
    }

    private Integer parseClassificationPrefix(StockCsvData csvData) {
        Integer prefix = classificationParser.parsePrefix(csvData.getSubjectCode());
        if(prefix == null)
            log.trace("분류번호 필터링됨 : {}", csvData);
        return prefix;
    }
}
