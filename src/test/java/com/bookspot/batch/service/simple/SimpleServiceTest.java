package com.bookspot.batch.service.simple;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

//@SpringBootTest
class SimpleServiceTest {
    @Autowired
    Top50BooksService top50BooksService;

//    @Test
    void test() {
        LocalDate monday = LocalDate.of(2025, 8, 18);
        top50BooksService.updateTop50Books(monday);
    }

    /*@MockBean
    WeeklyTop50ApiRequester requester;

    @Test
    void test() {
        WeeklyTop50ResponseSpec.Doc e1 = new WeeklyTop50ResponseSpec.Doc();
        e1.setBookname("ㅋㅋㅋㅋ");
        e1.setIsbn13("123456789012");
        e1.setAuthors("aaa");
        when(requester.findTop50(any())).thenReturn(List.of(e1));
        LocalDate monday = LocalDate.of(2025, 8, 18);
        simpleService.simple(monday);
    }*/
}