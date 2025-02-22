package com.bookspot.batch.step.writer.file.stock;

import com.bookspot.batch.BookSpotFileConst;
import com.bookspot.batch.global.file.FileFormat;
import com.bookspot.batch.global.file.FileMetadata;

import java.time.LocalDate;

public class StockCsvMetadataCreator {

    public static final String DIRECTORY_NAME = BookSpotFileConst.ROOT_DIRECTORY.concat("/stock");
    public static final String MULTI_CSV_FILE_PATH = "file:".concat(DIRECTORY_NAME).concat("/*.csv");
//    public static final String DIRECTORY_NAME = BookSpotFileConst.ROOT_DIRECTORY.concat("/temp_directory");

    public static FileMetadata create(long libraryId, LocalDate referenceDate) {
        return new FileMetadata(
                StockFilenameUtil.create(new StockFilenameElement(libraryId, referenceDate)),
                DIRECTORY_NAME,
                FileFormat.CSV);
    }
}
