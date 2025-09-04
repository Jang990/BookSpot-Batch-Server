package com.bookspot.batch.step.book.api;

import com.bookspot.batch.data.Top50Book;
import com.bookspot.batch.data.Top50BookStrings;
import com.bookspot.batch.step.processor.TitleEllipsisConverter;
import com.bookspot.batch.step.processor.csv.IsbnValidator;
import com.bookspot.batch.step.processor.csv.TextEllipsiser;
import com.bookspot.batch.step.processor.csv.book.BookClassificationParser;
import com.bookspot.batch.step.processor.csv.book.YearParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.Year;

@Slf4j
@Component
@RequiredArgsConstructor
public class Top50BookApiProcessor implements ItemProcessor<Top50BookStrings, Top50Book> {
    private final YearParser yearParser;
    private final IsbnValidator isbnValidator;
    private final TextEllipsiser textEllipsiser;
    private final BookClassificationParser classificationParser;

    @Override
    public Top50Book process(Top50BookStrings item) throws Exception {
        if(isbnValidator.isInValid(item.isbn13()))
            return null;
        if(isNotNumberFormat(item.ranking())
                || isNotNumberFormat(item.loanCount()))
            return null;

        return new Top50Book(
                Integer.parseInt(item.ranking()),
                textEllipsiser.ellipsize(item.title().trim(), TitleEllipsisConverter.MAX_TITLE_LENGTH),
                item.authors().trim(),
                item.publisher().trim(),
                parseYear(item),
                item.isbn13().trim(),
                item.vol(),
                parseClassificationPrefix(item),
                Integer.parseInt(item.loanCount())
        );
    }

    private boolean isNotNumberFormat(String number) {
        try {
            Integer.parseInt(number);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private Year parseYear(Top50BookStrings item) {
        Integer yearNum = yearParser.parse(item.publicationYear());
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

    private Integer parseClassificationPrefix(Top50BookStrings item) {
        Integer prefix = classificationParser.parsePrefix(item.subjectCode());
        if(prefix == null)
            log.trace("분류번호 필터링됨 : {}", item);
        return prefix;
    }
}
