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
                    getString(row, LibraryExcelSpec.LIBRARY_CODE),
                    getString(row, LibraryExcelSpec.NAME),
                    getAddress(row),
                    getString(row, LibraryExcelSpec.TEL),
                    Double.parseDouble(getString(row, LibraryExcelSpec.LATITUDE)),
                    Double.parseDouble(getString(row, LibraryExcelSpec.LONGITUDE)),
                    getString(row, LibraryExcelSpec.HOMEPAGE),
                    getString(row, LibraryExcelSpec.CLOSED),
                    getString(row, LibraryExcelSpec.OPERATING_INFO)
            );
        });
        return reader;
    }

    private static String getAddress(String[] row) {
        return getString(row, LibraryExcelSpec.ADDRESS)
                .replace("&middot;", "·") // "경상남도 창원시 마산회원구 3&middot;15대로 558"
                .replaceAll("\\s{2,}", " "); // "경상북도  포항시..." - 공백 2개 이상
    }

    private static String getString(String[] row, LibraryExcelSpec spec) {
        return row[spec.index].trim();
    }
}
