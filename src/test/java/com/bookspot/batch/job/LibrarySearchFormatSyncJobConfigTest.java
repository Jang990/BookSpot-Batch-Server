package com.bookspot.batch.job;

import com.bookspot.batch.data.file.csv.IsbnSearchFormatFileSpec;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;


import static org.junit.jupiter.api.Assertions.*;

class LibrarySearchFormatSyncJobConfigTest {
    LibrarySearchFormatSyncJobConfig config = new LibrarySearchFormatSyncJobConfig(null, null, null, null);

    @Test
    void Reader는_빈문자열도_그냥_공백문자열로_읽어온다() throws Exception {
        String target = "src/test/resources/files/sample/librarySync/libraryHomePages_Prod_Result.csv";
        FlatFileItemReader<IsbnSearchFormatFileSpec> reader = config.librarySearchFormatCsvReader(target);

        reader.open(new ExecutionContext());
        assertCsvData(reader.read(), 1,"2.28도서관","https://library.daegu.go.kr/228lib/intro/search/index.do?menu_idx=125&viewPage=1&booktype=BOOKANDNONBOOK&isbn=");
        assertCsvData(reader.read(), 177, "고양시립원당도서관", "");
        assertCsvData(reader.read(), 181, "고양시립한뫼도서관", "https://www.goyanglib.or.kr/center/menu/10004/program/30002/searchResultList.do?searchType=DETAIL&searchManageCodeArr=MK&searchAdvIsbn=");
        assertNull(reader.read());
        reader.close();
    }

    @Test
    void Processor는_공백문자를_거른다() throws Exception {
        ItemProcessor<IsbnSearchFormatFileSpec, IsbnSearchFormatFileSpec> processor = config.librarySearchFormatCsvProcessor();

        assertNull(processor.process(new IsbnSearchFormatFileSpec(null, "ABC", "ABC")));
        assertNull(processor.process(new IsbnSearchFormatFileSpec(1L, null, "ABC")));
        assertNull(processor.process(new IsbnSearchFormatFileSpec(1L, "", "ABC")));
        assertNull(processor.process(new IsbnSearchFormatFileSpec(1L, "ABC", null)));
        assertNull(processor.process(new IsbnSearchFormatFileSpec(1L, "ABC", "")));
    }

    private void assertCsvData(IsbnSearchFormatFileSpec data, int id, String name, String format) {
        assertEquals(data.id(), id);
        assertEquals(data.name(), name);
        assertEquals(data.format(), format);
    }

}