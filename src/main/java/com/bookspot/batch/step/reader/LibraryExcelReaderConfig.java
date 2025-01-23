package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.Library;
import com.bookspot.batch.step.reader.file.excel.library.LibraryExcelRowMapper;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class LibraryExcelReaderConfig {
    @Bean
    public PoiItemReader<Library> libraryExcelReader() {
        PoiItemReader<Library> reader = new PoiItemReader<>();
        reader.setName("libraryExcelReader");
        reader.setResource(new FileSystemResource(LibraryExcelConst.metadata.absolutePath()));
        reader.setLinesToSkip(8);
        reader.setRowMapper(new LibraryExcelRowMapper());
        return reader;
    }
}
