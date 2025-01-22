package com.bookspot.batch.stock.file;

import com.bookspot.batch.global.file.NaruFileDownloader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockFileDownloader {
    private final NaruFileDownloader downloader;

    public void download(StockFileData stockFileData) {
        downloader.downloadSync(
                stockFileData.filePath(),
                StockCsvMetadataCreator.create(stockFileData.libraryCode()));
    }
}
