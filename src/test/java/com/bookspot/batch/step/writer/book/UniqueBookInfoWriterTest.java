package com.bookspot.batch.step.writer.book;

import com.bookspot.batch.BookTestDataBuilder;
import com.bookspot.batch.SpringBootWithH2Test;
import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.step.service.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    void test1() throws Exception {
        ConvertedUniqueBook item = new BookTestDataBuilder()
                .isbn("1234567890123")
                .build();

        uniqueBookInfoWriter.write(Chunk.of(item));
        uniqueBookInfoWriter.write(Chunk.of(item));

        assertEquals(1L, bookRepository.count());
    }

}