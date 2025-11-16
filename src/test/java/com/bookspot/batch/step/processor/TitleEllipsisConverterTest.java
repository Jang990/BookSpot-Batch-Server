package com.bookspot.batch.step.processor;

import com.bookspot.batch.StockCsvDataBuilder;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.csv.TextEllipsiser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TitleEllipsisConverterTest {
    @Mock TextEllipsiser textEllipsiser;
    @InjectMocks TitleEllipsisConverter titleEllipsisConverter;

    @Test
    void 제목이_없다면_원본_반환() throws Exception {
        StockCsvData original = new StockCsvDataBuilder().title(null).build();
        StockCsvData result = titleEllipsisConverter.process(original);
        assertEquals(original, result);
    }

    @Test
    void 제목이_200자_이하면_원본_반환() throws Exception {
        StockCsvData original = new StockCsvDataBuilder().title("A".repeat(200)).build();
        StockCsvData result = titleEllipsisConverter.process(original);
        assertEquals(original, result);
    }

    @Test
    void 제목이_200자를_초과하면_textEllipsiser의_결과로_제목변환() throws Exception {
        StockCsvData original = new StockCsvDataBuilder().title("A".repeat(201)).build();
        when(textEllipsiser.ellipsize(anyString(), anyInt()))
                .thenReturn("변경된 제목...");

        StockCsvData result = titleEllipsisConverter.process(original);

        assertEquals("변경된 제목...", result.getTitle());

        // 다른 필드는 모두 값이 같음
        assertEquals(original.getAuthor(), result.getAuthor());
        assertEquals(original.getIsbn(), result.getIsbn());
        assertEquals(original.getPublisher(), result.getPublisher());
    }

}