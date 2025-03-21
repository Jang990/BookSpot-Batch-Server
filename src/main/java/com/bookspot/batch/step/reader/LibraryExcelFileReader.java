package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.Library;
import com.bookspot.batch.step.reader.file.excel.library.LibraryExcelRowMapper;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.core.io.FileSystemResource;

public class LibraryExcelFileReader extends PoiItemReader<Library> {

    public LibraryExcelFileReader(LibraryExcelRowMapper libraryExcelRowMapper) {
        setName("libraryExcelFileReader");
        setResource(new FileSystemResource(LibraryExcelConst.metadata.absolutePath()));
        setLinesToSkip(8);
        setRowMapper(libraryExcelRowMapper);
    }

}
