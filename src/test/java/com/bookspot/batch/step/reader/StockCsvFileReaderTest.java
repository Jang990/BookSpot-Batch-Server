package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.file.csv.StockCsvData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.FileSystemResource;

import static org.junit.jupiter.api.Assertions.*;

class StockCsvFileReaderTest {
    private StockCsvFileReader reader;
    private final FileSystemResource testResource = new FileSystemResource(
            "src/test/resources/files/reader/book/StockCsvFileReaderTestFile.csv"
    );

    @BeforeEach
    void beforeEach() throws Exception {
        reader = new StockCsvFileReader(testResource);
        reader.open(new ExecutionContext());
    }

    @AfterEach
    void afterEach() {
        if (reader != null) {
            reader.close();
        }
    }

    /*
    읽는데만 초점을 두기 때문에 값 변환은 최대한 자제함.
    [책 개수, 대출 수] 숫자 변환만 실시

    번호,도서명,저자,출판사,발행년도,ISBN,세트 ISBN,부가기호,권,주제분류번호,도서권수,대출건수,등록일자,
    "1","(놀라운) 호랑이 빵집","서지원 글 ;홍그림 그림","아르볼","2024","0000000000001","","","4","813.8","1","2","2024-11-08",
    "2","의사 어벤저스","고희정 글;조승연 그림","가나출판사","2024","0000000000002","","7","20","510.4","1","1","2024-11-08",
     */
    @Test
    void 장서현황_CSV파일을_DTO로_변환() throws Exception {
        StockCsvData result = reader.read();
        assertEquals("(놀라운) 호랑이 빵집", result.getTitle());
        assertEquals("서지원 글 ;홍그림 그림", result.getAuthor());
        assertEquals("아르볼", result.getPublisher());
        assertEquals("2024", result.getPublicationYear());
        assertEquals("0000000000001", result.getIsbn());
        assertEquals("4", result.getVolume());
        assertEquals("813.8", result.getSubjectCode());
        assertEquals(1, result.getNumberOfBooks());
        assertEquals(2, result.getLoanCount());

        result = reader.read();
        assertEquals("의사 어벤저스", result.getTitle());
        assertEquals("고희정 글;조승연 그림", result.getAuthor());
        assertEquals("가나출판사", result.getPublisher());
        assertEquals("2024", result.getPublicationYear());
        assertEquals("0000000000002", result.getIsbn());
        assertEquals("20", result.getVolume());
        assertEquals("510.4", result.getSubjectCode());
        assertEquals(1, result.getNumberOfBooks());
        assertEquals(1, result.getLoanCount());

        assertNull(reader.read());
    }

}