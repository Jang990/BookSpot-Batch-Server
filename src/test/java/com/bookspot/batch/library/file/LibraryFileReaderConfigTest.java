package com.bookspot.batch.library.file;

import com.bookspot.batch.library.data.Library;
import com.bookspot.batch.library.reader.file.LibraryFileReaderConfig;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.ExecutionContext;

class LibraryFileReaderConfigTest {
    LibraryFileReaderConfig config = new LibraryFileReaderConfig();

//    @Test
    void test() throws Exception {
        PoiItemReader<Library> reader = config.libraryExcelReader();
        reader.open(new ExecutionContext());
        System.out.println(reader.read());
        System.out.println();
        System.out.println(reader.read());
    }

}