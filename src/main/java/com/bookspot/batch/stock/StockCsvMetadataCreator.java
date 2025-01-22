package com.bookspot.batch.stock;

import com.bookspot.batch.BookSpotFileConst;
import com.bookspot.batch.global.file.FileFormat;
import com.bookspot.batch.global.file.FileMetadata;

public class StockCsvMetadataCreator {

    private static final String DIRECTORY_NAME = BookSpotFileConst.ROOT_DIRECTORY.concat("/stock");

    public static FileMetadata create(String libraryCode) {
        return new FileMetadata(libraryCode, DIRECTORY_NAME, FileFormat.CSV);
    }
}
