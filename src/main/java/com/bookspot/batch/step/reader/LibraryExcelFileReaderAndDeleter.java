package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.Library;
import com.bookspot.batch.global.FileService;
import com.bookspot.batch.step.reader.file.excel.library.LibraryExcelRowMapper;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class LibraryExcelFileReaderAndDeleter extends PoiItemReader<Library> {
    private final Resource targetResource;
    private final FileService fileService;

    /* Caused by: java.lang.ArrayIndexOutOfBoundsException: Index 9 out of bounds for length 1 예외 시 setLinesToSkip 조절 필요 */
    public LibraryExcelFileReaderAndDeleter(
            FileService fileService,
            LibraryExcelRowMapper libraryExcelRowMapper,
            String libraryFilePath
    ) {
        setName("LibraryExcelFileReaderAndDeleter");
        setLinesToSkip(8);
        setRowMapper(libraryExcelRowMapper);

        this.fileService = fileService;
        this.targetResource = new FileSystemResource(libraryFilePath);
        setResource(targetResource);
    }

    @Override
    public void close() throws ItemStreamException {
        super.close();
        fileService.delete(targetResource);
    }
}
