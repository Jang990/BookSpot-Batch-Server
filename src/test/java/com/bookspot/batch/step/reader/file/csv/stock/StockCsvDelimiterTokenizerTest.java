package com.bookspot.batch.step.reader.file.csv.stock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockCsvDelimiterTokenizerTest {
    StockCsvDelimiterTokenizer tokenizer = new StockCsvDelimiterTokenizer();

    @Test
    void 콤마로_데이터를_구분() {
        String[] result = tokenizer.tokenize(
                "A,B,C"
        ).getValues();

        assertEquals("A", result[0]);
        assertEquals("B", result[1]);
        assertEquals("C", result[2]);

        // 값이 적게 주어지면 null로 채워짐
        assertNull(result[3]);
    }
    @Test
    void 값의_주위_공백제거() {
        String[] result = tokenizer.tokenize(
                " 주위 공백을 제거 , 어디든 좌우 공백 제거 "
        ).getValues();

        assertEquals("주위 공백을 제거", result[0]);
        assertEquals("어디든 좌우 공백 제거", result[1]);
    }

    @Test
    void 따옴표_안에_값은_따옴표를_제외하고_가져온다() {
        String[] result = tokenizer.tokenize(
                "\"제목 테스트\""
        ).getValues();

        assertEquals("제목 테스트", result[0]);
    }

    @Test
    void 따옴표안에_콤마는_무시된다() {
        String[] result = tokenizer.tokenize(
                "1,\"콤마,무시해라\",무시됐다."
        ).getValues();

        assertEquals("1", result[0]);
        assertEquals("콤마,무시해라", result[1]);
        assertEquals("무시됐다.", result[2]);
    }
}