package com.bookspot.batch.step.reader.api.top50;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class WeeklyTop50ApiRequesterTest {

    @Autowired
    WeeklyTop50ApiRequester requester;

    @Test
    void test() {
        List<WeeklyTop50ResponseSpec.Doc> result = requester.findTop50(LocalDate.of(2025, 8, 18));
        for (WeeklyTop50ResponseSpec.Doc doc : result) {
            System.out.println(doc);
        }
    }

}