package com.bookspot.batch.global.file;

import org.springframework.core.io.Resource;

public class StockCsvMetadataHelper {
    public static long parseLibraryId(Resource stockCsv) {
        String filename = stockCsv.getFilename();
        return Long.parseLong(filename.split("_")[0]);
    }
}
