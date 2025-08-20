package com.bookspot.batch.step.book.api;

import com.bookspot.batch.data.Top50Book;
import com.bookspot.batch.step.reader.api.top50.RankingConditions;
import com.bookspot.batch.step.reader.api.top50.WeeklyTop50ApiRequester;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class Top50BookApiReader implements ItemReader<Top50Book> {
    private final LocalDate referenceDate;
    private final RankingConditions rankingConditions;
    private final WeeklyTop50ApiRequester weeklyTop50ApiRequester;

    private List<Top50Book> result;
    private int currentIndex;

    @Override
    public Top50Book read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(result == null)
            result = weeklyTop50ApiRequester.findTop50(referenceDate);
        if(currentIndex >= result.size())
            return null;
        return result.get(currentIndex++);
    }
}
