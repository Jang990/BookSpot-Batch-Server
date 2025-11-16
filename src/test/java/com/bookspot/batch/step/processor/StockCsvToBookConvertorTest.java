package com.bookspot.batch.step.processor;

import com.bookspot.batch.StockCsvDataBuilder;
import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.csv.book.BookClassificationParser;
import com.bookspot.batch.step.processor.csv.book.YearParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockCsvToBookConvertorTest {
    @Mock BookClassificationParser classificationParser;
    @Mock YearParser yearParser;
    @InjectMocks StockCsvToBookConvertor convertor;

    @Test
    @DisplayName("정상처리 - 분류번호와 연도를 파싱해서 반환")
    void test1() throws Exception {
        when(classificationParser.parsePrefix(anyString())).thenReturn(460);
        when(yearParser.parse(anyString())).thenReturn(2024);
        StockCsvData original = StockCsvDataBuilder.newSample();

        ConvertedUniqueBook result = convertor.process(original);

        // ConvertedUniqueBook에서 Title은 Volume과 결합됨 - 현재 테스트 범위를 벗어남.
        // assertNotEquals(original.getTitle(), result.getTitle());
        assertEquals(460, result.getSubjectCode());
        assertEquals(Year.of(2024), result.getPublicationYear());
    }

}