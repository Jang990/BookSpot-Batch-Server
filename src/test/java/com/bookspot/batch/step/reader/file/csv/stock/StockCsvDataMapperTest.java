package com.bookspot.batch.step.reader.file.csv.stock;

import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.global.file.spec.LibraryStockCsvSpec;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StockCsvDataMapperTest {

    StockCsvDataMapper mapper = new StockCsvDataMapper();

    // 기본적으로 null이 안들어온다는 가정하에 만들어짐.
    @Test
    void 책_개수와_대출_수는_공백이라면_0으로_초기화() throws BindException {
        FieldSet fieldSet = mock(FieldSet.class);
        when(fieldSet.readString(LibraryStockCsvSpec.NUMBER_OF_BOOKS.value())).thenReturn("");
        when(fieldSet.readString(LibraryStockCsvSpec.LOAN_COUNT.value())).thenReturn(" ");

        StockCsvData result = mapper.mapFieldSet(fieldSet);
        assertEquals(0, result.getNumberOfBooks());
        assertEquals(0, result.getLoanCount());
    }

}