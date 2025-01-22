package com.bookspot.batch.stock.file;

import com.bookspot.batch.global.file.FileFormat;
import com.bookspot.batch.global.file.FileMetadata;

class StockCsvMetadataCreator {
    private static final String DIRECTORY_NAME = "bookSpotFiles/stock";

    protected static FileMetadata create(String libraryCode) {
        return new FileMetadata(libraryCode, DIRECTORY_NAME, FileFormat.CSV);
    }
}
