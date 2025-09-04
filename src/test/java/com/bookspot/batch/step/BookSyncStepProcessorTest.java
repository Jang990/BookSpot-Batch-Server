package com.bookspot.batch.step;

import com.bookspot.batch.StockCsvDataBuilder;
import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.TitleEllipsisConverter;
import com.bookspot.batch.step.processor.csv.IsbnValidator;
import com.bookspot.batch.step.processor.StockCsvToBookConvertor;
import com.bookspot.batch.step.processor.csv.TextEllipsiser;
import com.bookspot.batch.step.processor.csv.book.BookClassificationParser;
import com.bookspot.batch.step.processor.csv.book.YearParser;
import com.bookspot.batch.step.processor.IsbnValidationFilter;
import com.bookspot.batch.step.processor.InMemoryIsbnFilter;
import com.bookspot.batch.step.processor.exception.InvalidIsbn13Exception;
import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.support.CompositeItemProcessor;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookSyncStepProcessorTest {
    @Mock IsbnSet isbnSet;
    CompositeItemProcessor<StockCsvData, ConvertedUniqueBook> processor;

    BookSyncStepConfig config = new BookSyncStepConfig(null, null, null, null);

    @BeforeEach
    void beforeEach() {
        processor = config.bookSyncProcessor(
                new IsbnValidationFilter(new IsbnValidator(), 1L),
                new InMemoryIsbnFilter(isbnSet),
                new TitleEllipsisConverter(new TextEllipsiser()),
                new StockCsvToBookConvertor(new BookClassificationParser(), new YearParser())
        );
    }

    /** @see TitleEllipsisConverter */
    @Test
    void _200자보다_긴_타이틀은_생략기호로_변경() throws Exception {
        StockCsvData csvData = new StockCsvDataBuilder()
                .title("ABCDEFGHIJ".repeat(20) + "E")
                .volume(null)
                .build();

        assertEquals(
                "ABCDEFGHIJ".repeat(19) + "ABCDEFG...",
                processor.process(csvData).getTitle()
        );
    }

    /** @see IsbnValidationFilter */
    @Test
    void 잘못된_ISBN은_필터링됨() throws Exception {
        Assertions.assertThrows(InvalidIsbn13Exception.class,
                () -> processor.process(
                        new StockCsvDataBuilder()
                                .isbn("xxx0000000000")
                                .build())
        );
    }

    /** @see InMemoryIsbnFilter */
    @Test
    void 이미_DB에서_불러와서_메모리에_이미_존재하는_ISBN은_필터링() throws Exception {
        when(isbnSet.contains("0000000000000")).thenReturn(true);
        assertNull(processor.process(new StockCsvDataBuilder()
                .isbn("0000000000000").build()));
    }

    /** @see StockCsvToBookConvertor */
    @Test
    void 볼륨이_있다면_타이틀에_연결() throws Exception {
        StockCsvData csvData = new StockCsvDataBuilder()
                .title("해리포터")
                .volume("2권")
                .build();

        assertEquals("해리포터 (2권)", processor.process(csvData).getTitle());
    }

    /** @see StockCsvToBookConvertor */
    @Test
    void 연도를_파싱해서_변환() throws Exception {
        StockCsvData csvData = new StockCsvDataBuilder()
                .year("X2024년")
                .build();

        assertEquals(Year.of(2024),
                processor.process(csvData).getPublicationYear());
    }

    /** @see StockCsvToBookConvertor */
    @Test
    void 분류_코드를_파싱해서_변환() throws Exception {
        StockCsvData csvData = new StockCsvDataBuilder()
                .subjectCode("123.5678")
                .build();

        assertEquals(123, processor.process(csvData).getSubjectCode());
    }
}
