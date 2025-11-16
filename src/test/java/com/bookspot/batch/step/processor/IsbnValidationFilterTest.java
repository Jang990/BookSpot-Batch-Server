package com.bookspot.batch.step.processor;

import com.bookspot.batch.StockCsvDataBuilder;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.csv.IsbnValidator;
import com.bookspot.batch.step.processor.exception.InvalidIsbn13Exception;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IsbnValidationFilterTest {
    @Mock IsbnValidator isbnValidator;

    StockCsvData data = StockCsvDataBuilder.newSample();

    @Test
    void isbn13이_유효하지_않으면_예외처리() throws Exception {
        long tempLibraryId = 1L;
        IsbnValidationFilter isbnValidationFilter = new IsbnValidationFilter(
                isbnValidator, tempLibraryId
        );
        when(isbnValidator.isInValid(data.getIsbn()))
                .thenReturn(true);

        InvalidIsbn13Exception exception = assertThrows(
                InvalidIsbn13Exception.class,
                () -> isbnValidationFilter.process(data)
        );
        assertEquals(tempLibraryId, exception.getLibraryId());
    }

    @Test
    void 도서관_파일의_이름에_ID를_예외정보로_반환한다() {
        Resource file = mock(Resource.class);
        when(file.getFilename()).thenReturn("1234567_2025-01-01");
        IsbnValidationFilter isbnValidationFilter = new IsbnValidationFilter(
                isbnValidator, file
        );

        when(isbnValidator.isInValid(anyString())).thenReturn(true);

        InvalidIsbn13Exception exception = assertThrows(
                InvalidIsbn13Exception.class,
                () -> isbnValidationFilter.process(data)
        );
        assertEquals(1234567L, exception.getLibraryId());
    }
}