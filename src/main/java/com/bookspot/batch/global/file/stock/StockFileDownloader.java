package com.bookspot.batch.global.file.stock;

import com.bookspot.batch.global.file.NaruFileDownloader;
import com.bookspot.batch.data.crawler.StockFileData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockFileDownloader {
    private final NaruFileDownloader downloader;

    public void download(StockFileData stockFileData) {
        downloader.downloadSync(
                stockFileData.filePath(),
                StockCsvMetadataCreator.create(stockFileData.libraryId(), stockFileData.stockUpdatedAt()));
    }
}
