package com.bookspot.batch.step.reader.file.excel.library;

import com.bookspot.batch.global.crawler.naru.NaruRequestCreator;
import com.bookspot.batch.global.file.NaruFileDownloader;
import com.bookspot.batch.step.reader.LibraryExcelConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LibraryFileDownloader {
    private final NaruRequestCreator requestCreator;
    private final NaruFileDownloader naruFileDownloader;
    private final String targetFilePath;

    private static final String URL = "https://www.data4library.kr/libDataDownload";

    public void download() {
        naruFileDownloader.downloadSync(URL, requestCreator.create(), targetFilePath);
    }

    public void delete() {
        if(naruFileDownloader.deleteFile(targetFilePath))
            log.info("도서관 CSV 파일 삭제 완료");
        else
            log.warn("도서관 CSV 파일 삭제 실패");
    }
}
