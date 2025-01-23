package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.Library;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.ExecutionContext;

class LibraryFileReaderConfigTest {
    LibraryExcelReaderConfig config = new LibraryExcelReaderConfig();

//    @Test
    void test() throws Exception {
        PoiItemReader<Library> reader = config.libraryExcelReader();
        reader.open(new ExecutionContext());
        System.out.println(reader.read());
        System.out.println();
        System.out.println(reader.read());
    }

}