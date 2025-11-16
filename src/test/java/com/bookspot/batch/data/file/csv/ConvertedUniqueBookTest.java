package com.bookspot.batch.data.file.csv;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class ConvertedUniqueBookTest {

    @Test
    @DisplayName("제목과 볼륨을 제목(볼륨) 포맷으로 합쳐줌")
    void test1() {
        ConvertedUniqueBook book = create("정상책", "2권");
        assertEquals("정상책 (2권)", book.getTitle());
    }

    private static ConvertedUniqueBook create(String title, String volume) {
        return new ConvertedUniqueBook("", title, "", "", volume, 0, 0, Year.of(1234));
    }

}