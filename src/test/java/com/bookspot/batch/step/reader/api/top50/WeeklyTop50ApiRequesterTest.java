package com.bookspot.batch.step.reader.api.top50;

import com.bookspot.batch.data.Top50Book;
import com.bookspot.batch.data.document.RankingAge;
import com.bookspot.batch.data.document.RankingGender;
import com.bookspot.batch.data.document.RankingType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
class WeeklyTop50ApiRequesterTest {

    @Autowired
    WeeklyTop50ApiRequester requester;

//    @Test
    void 주간_대출_수_top50_API_검증() {
        List<Top50Book> result = requester.findTop50(
                LocalDate.of(2025, 8, 18),
                new RankingConditions(RankingType.WEEKLY, RankingGender.ALL, RankingAge.ALL)
        );
        assertEquals(50, result.size());

        assertEquals("소년이 온다 :한강 장편소설", result.getFirst().title());
        assertEquals(1, result.getFirst().ranking());
        assertEquals("9788936434120", result.getFirst().isbn13());

        assertEquals("흔한남매 이상한 나라의 고전 읽기", result.getLast().title());
        assertEquals(50, result.getLast().ranking());
        assertEquals("9791168416314", result.getLast().isbn13());
    }

}