package com.bookspot.batch.step.writer.book;

import com.bookspot.batch.SpringBootWithH2Test;
import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.step.service.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootWithH2Test
class UniqueBookInfoWriterTest {
    @Autowired
    private UniqueBookInfoWriter uniqueBookInfoWriter;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Insert Ignore기 때문에 유니크한 ISBN은 하나만 저장됨")
    void test1() throws Exception {
        ConvertedUniqueBook item = new ConvertedUniqueBook(
                "1234567890123",
                "임시 제목", "임시 저자", "임시 출판사",
                null, 0, 0, Year.of(2025)
        );
        uniqueBookInfoWriter.write(Chunk.of(item));
        uniqueBookInfoWriter.write(Chunk.of(item));

        assertEquals(1L, bookRepository.count());
    }

}