package com.bookspot.batch.step.writer.file.stock;

import com.bookspot.batch.BookSpotFileConst;
import com.bookspot.batch.global.file.FileFormat;
import com.bookspot.batch.global.file.FileMetadata;

import java.time.LocalDate;

public class StockCsvMetadataCreator {

    public static final String DIRECTORY_NAME = BookSpotFileConst.ROOT_DIRECTORY.concat("/stock");

    public static FileMetadata create(long libraryId, LocalDate date) {
        return new FileMetadata(
                String.format("%d_%s", libraryId, date),
                DIRECTORY_NAME,
                FileFormat.CSV);
    }
}
