package com.bookspot.batch.step.processor;

import com.bookspot.batch.StockCsvDataBuilder;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.csv.IsbnValidator;
import com.bookspot.batch.step.processor.exception.InvalidIsbn13Exception;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IsbnValidationFilterTest {
    @Mock IsbnValidator isbnValidator;
    long tempLibraryId = 1L;

    IsbnValidationFilter isbnValidationFilter;

    @BeforeEach
    void beforeEach() {
        isbnValidationFilter = new IsbnValidationFilter(isbnValidator, tempLibraryId);
    }


    @Test
    void isbn13이_유효하지_않으면_예외처리() throws Exception {
        StockCsvData data = new StockCsvDataBuilder().build();
        Mockito.when(isbnValidator.isInValid(data.getIsbn()))
                .thenReturn(true);

        assertThrows(
                InvalidIsbn13Exception.class,
                () -> isbnValidationFilter.process(data)
        );
    }
}