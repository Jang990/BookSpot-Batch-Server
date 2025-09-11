package com.bookspot.batch.step.processor;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.csv.TextEllipsiser;
import com.bookspot.batch.step.service.memory.bookid.IsbnMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockProcessorTest {
    @Mock
    private IsbnMemoryRepository isbnMemoryRepository;
    private TextEllipsiser textEllipsiser = new TextEllipsiser();
    private StockProcessor stockProcessor;

    private final long libraryId = 1L;

    @BeforeEach
    void setUp() {
        stockProcessor = new StockProcessor(textEllipsiser, isbnMemoryRepository, libraryId);
    }

    @Test
    void 책_id가_없으면_null_반환() throws Exception {
        StockCsvData item = mock(StockCsvData.class);
        when(isbnMemoryRepository.get(item.getIsbn())).thenReturn(null);

        LibraryStock result = stockProcessor.process(item);

        assertNull(result);
    }

    @Test
    void 유효하면_LibraryStock_현재_Processor의_LibraryId와_함께_반환() throws Exception {
        StockCsvData item = mock(StockCsvData.class);
        when(isbnMemoryRepository.get(item.getIsbn())).thenReturn(42L);
        when(item.getSubjectCode()).thenReturn("123.4");

        LibraryStock result = stockProcessor.process(item);

        assertNotNull(result);
        assertEquals(libraryId, result.getLibraryId());
        assertEquals(42L, result.getBookId());
        assertEquals("123.4", result.getSubjectCode());
    }

    @Test
    void subjectCode에_쉼표가_있으면_null처리() throws Exception {
        StockCsvData item = mock(StockCsvData.class);
        when(isbnMemoryRepository.get(item.getIsbn())).thenReturn(42L);
        when(item.getSubjectCode()).thenReturn(",123.4");


        LibraryStock result = stockProcessor.process(item);

        assertNotNull(result);
        assertNull(result.getSubjectCode());
    }

    @Test
    void subjectCode가_40자_초과하면_점을_붙혀줌() throws Exception {
        StockCsvData item = mock(StockCsvData.class);
        when(item.getSubjectCode()).thenReturn("123".repeat(14));
        when(isbnMemoryRepository.get(item.getIsbn())).thenReturn(42L);

        LibraryStock result = stockProcessor.process(item);

        assertNotNull(result);
        assertTrue(result.getSubjectCode().startsWith("123"));
        assertTrue(result.getSubjectCode().endsWith(TextEllipsiser.ELLIPSIS));
        assertTrue(result.getSubjectCode().length() == 40);
    }

}