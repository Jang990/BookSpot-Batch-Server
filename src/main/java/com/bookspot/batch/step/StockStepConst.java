package com.bookspot.batch.step;

public class StockStepConst {
    public static final String STEP_NAME = "libraryStockStep";
    public static final String DOWNLOAD_STEP_NAME = "stockCsvDownloadStep";

    protected static final int CHUNK_SIZE = 1000;
    public static final int DOWNLOAD_CHUNK_SIZE = 10;
}
