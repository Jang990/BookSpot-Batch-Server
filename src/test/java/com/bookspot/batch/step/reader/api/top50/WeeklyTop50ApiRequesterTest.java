package com.bookspot.batch.step.reader.api.top50;

import com.bookspot.batch.data.Top50Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

//@SpringBootTest
class WeeklyTop50ApiRequesterTest {

    @Autowired
    WeeklyTop50ApiRequester requester;

//    @Test
    void test() {
        List<Top50Book> result = requester.findTop50(LocalDate.of(2025, 8, 18));
        for (Top50Book doc : result) {
            System.out.println(doc);
        }
    }

}