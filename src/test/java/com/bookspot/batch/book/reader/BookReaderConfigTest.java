package com.bookspot.batch.book.reader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;

import static org.junit.jupiter.api.Assertions.*;

class BookReaderConfigTest {
    BookReaderConfig config = new BookReaderConfig();

    @Test
    @DisplayName("파일 리딩 가능")
    void test1() throws Exception {
        FlatFileItemReader<BookCsvData> reader = config.bookCsvFileItemReader(/*SAMPLE_RESOURCE*/);
        reader.open(new ExecutionContext());

        assertEquals("(Why)지구", reader.read().getTitleName());
        assertEquals("(Why)동물", reader.read().getTitleName());
        assertEquals("(Why)미래과학", reader.read().getTitleName());
        assertEquals("(Why)외계인과 UFO", reader.read().getTitleName());
    }
}