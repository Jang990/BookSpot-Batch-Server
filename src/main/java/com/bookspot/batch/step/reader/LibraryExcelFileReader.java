package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.Library;
import com.bookspot.batch.step.reader.file.excel.library.LibraryExcelRowMapper;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.core.io.FileSystemResource;

public class LibraryExcelFileReader extends PoiItemReader<Library> {

    /* Caused by: java.lang.ArrayIndexOutOfBoundsException: Index 9 out of bounds for length 1 예외 시 setLinesToSkip 조절 필요 */
    public LibraryExcelFileReader(LibraryExcelRowMapper libraryExcelRowMapper, String libraryFilePath) {
        setName("libraryExcelFileReader");
        setResource(new FileSystemResource(libraryFilePath));
        setLinesToSkip(8);
        setRowMapper(libraryExcelRowMapper);
    }

}
