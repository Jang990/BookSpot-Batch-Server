package com.bookspot.batch.step.processor;

import com.bookspot.batch.StockCsvDataBuilder;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InMemoryIsbnFilterTest {

    @Mock IsbnSet isbnSet;
    @InjectMocks InMemoryIsbnFilter inMemoryIsbnFilter;

    StockCsvData sample = StockCsvDataBuilder.newSample();

    @Test
    void IsbnSet에_존재하는_ISBN은_필터링() throws Exception {
        when(isbnSet.contains(anyString())).thenReturn(true);
        assertNull(inMemoryIsbnFilter.process(sample));
    }

    @Test
    void 정상일_땐_데이터_변경_없이_원본_반환() throws Exception {
        when(isbnSet.contains(anyString())).thenReturn(false);
        StockCsvData result = inMemoryIsbnFilter.process(sample);
        assertEquals(sample, result);
    }

}