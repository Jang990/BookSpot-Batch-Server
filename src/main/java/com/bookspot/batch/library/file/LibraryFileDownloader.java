package com.bookspot.batch.library.file;

import com.bookspot.batch.global.crawler.naru.NaruRequestCreator;
import com.bookspot.batch.global.file.FileFormat;
import com.bookspot.batch.global.file.FileMetaData;
import com.bookspot.batch.global.file.NaruFileDownloader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LibraryFileDownloader {
    private final NaruRequestCreator requestCreator;
    private final NaruFileDownloader naruFileDownloader;

    private static final String URL = "https://www.data4library.kr/libDataDownload";

    public void download() {
        naruFileDownloader.downloadSync(
                URL,
                requestCreator.create(),
                LibraryExcelConst.metadata);
    }
}
