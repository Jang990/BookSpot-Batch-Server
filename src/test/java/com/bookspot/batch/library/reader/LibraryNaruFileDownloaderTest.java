package com.bookspot.batch.library.reader;

import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import com.bookspot.batch.global.crawler.naru.NaruRequestCreator;
import com.bookspot.batch.global.file.NaruFileDownloader;
import com.bookspot.batch.library.reader.file.LibraryFileDownloader;

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