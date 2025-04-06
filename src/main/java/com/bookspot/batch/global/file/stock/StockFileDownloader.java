package com.bookspot.batch.global.file.stock;

import com.bookspot.batch.global.file.FileFormat;
import com.bookspot.batch.global.file.NaruFileDownloader;
import com.bookspot.batch.data.crawler.StockFileData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StockFileDownloader {
    private final NaruFileDownloader downloader;
    private final String downloadDir;

    public void download(StockFileData stockFileData) {
        String outputFilePath = downloadDir.concat("/")
                .concat(fileName(stockFileData))
                .concat(FileFormat.CSV.getExt());

        downloader.downloadSync(stockFileData.filePath(), outputFilePath);
    }

    private static String fileName(StockFileData stockFileData) {
        return StockFilenameUtil.create(
                new StockFilenameElement(
                        stockFileData.libraryId(),
                        stockFileData.stockUpdatedAt()
                )
        );
    }
}
