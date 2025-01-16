package com.bookspot.batch.book.reader;

import com.bookspot.batch.book.data.Book;
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
        skip(reader, 46);

        BookCsvData result = reader.read();
        assertEquals("(한·양방) 똑똑한 병원이용", result.getTitle());
        assertEquals("517.16", result.getSubjectCode());
        assertEquals("백태선 지음", result.getAuthor());
        assertEquals("9788991373273", result.getIsbn13());
        assertEquals("2008", result.getPublicationYear());
        assertNull(result.getVolume());

        result = reader.read();
        assertEquals("당신도 건강하게 100세 이상 살 수 있다", result.getTitle());
        assertEquals("517", result.getSubjectCode());
        assertNull(result.getAuthor());
        assertEquals("9788988314944", result.getIsbn13());
        assertEquals("2002", result.getPublicationYear());
        assertEquals("2", result.getVolume());
    }

    private static void skip(FlatFileItemReader<BookCsvData> reader, int cnt) throws Exception {
        for (int i = 0; i < cnt; i++)
            reader.read();
    }
}