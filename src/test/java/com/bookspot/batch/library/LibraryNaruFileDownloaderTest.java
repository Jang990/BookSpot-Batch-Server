package com.bookspot.batch.library;

import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import com.bookspot.batch.global.crawler.naru.NaruRequestCreator;
import com.bookspot.batch.global.file.NaruFileDownloader;
import com.bookspot.batch.library.file.LibraryFileDownloader;
import org.junit.jupiter.api.Test;

class LibraryNaruFileDownloaderTest {
    LibraryFileDownloader libraryFileDownloader = new LibraryFileDownloader(
            new NaruRequestCreator(new JsoupCrawler()),
            new NaruFileDownloader()
    );

//    @Test
    void test() {
        libraryFileDownloader.download();
    }

}