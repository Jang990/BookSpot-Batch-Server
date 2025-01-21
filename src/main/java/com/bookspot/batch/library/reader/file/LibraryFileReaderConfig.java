package com.bookspot.batch.library.reader.file;

import com.bookspot.batch.library.data.Library;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class LibraryFileReaderConfig {
    @Bean
    public PoiItemReader<Library> libraryExcelReader() {
        PoiItemReader<Library> reader = new PoiItemReader<>();
        reader.setName("libraryExcelReader");
        reader.setResource(new FileSystemResource(LibraryExcelConst.metadata.absolutePath()));
        reader.setLinesToSkip(8);
        reader.setRowMapper(rs -> {
            String[] row = rs.getCurrentRow();
            return new Library(
                    row[LibraryExcelSpec.LIBRARY_CODE.index],
                    row[LibraryExcelSpec.NAME.index],
                    row[LibraryExcelSpec.ADDRESS.index],
                    row[LibraryExcelSpec.TEL.index],
                    Double.parseDouble(row[LibraryExcelSpec.LATITUDE.index]),
                    Double.parseDouble(row[LibraryExcelSpec.LONGITUDE.index]),
                    row[LibraryExcelSpec.HOMEPAGE.index],
                    row[LibraryExcelSpec.CLOSED.index],
                    row[LibraryExcelSpec.OPERATING_INFO.index]
            );
        });
        return reader;
    }
}
