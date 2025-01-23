package com.bookspot.batch.step.reader.crawler.library;

import com.bookspot.batch.global.crawler.common.CrawlingResult;
import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import com.bookspot.batch.global.crawler.common.exception.ElementNotFoundException;
import com.bookspot.batch.global.crawler.naru.NaruPagingUrlBuilder;
import com.bookspot.batch.step.LibraryStepConst;
import com.bookspot.batch.library.data.LibraryNaruDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaruDetailParser {
    private final JsoupCrawler jsoupCrawler;
    private final NaruPagingUrlBuilder urlBuilder;
    private final LibraryListPageConvertor libraryListPageConvertor;
    private final String LIBRARY_LIST_URL = "https://www.data4library.kr/libDataL";

    public List<LibraryNaruDetail> parseDetail(Pageable pageable) {
        CrawlingResult crawling = jsoupCrawler.get(urlBuilder.build(LIBRARY_LIST_URL, pageable));

        List<LibraryNaruDetail> result = new LinkedList<>();
        for (int elementNum = 1; elementNum <= LibraryStepConst.LIBRARY_CHUNK_SIZE; elementNum++) {
            try {
                result.add(convert(crawling, elementNum));
            } catch (ElementNotFoundException e) {
                log.info("도서관 리스트 페이지 파싱 종료 => {}, 종료 번호 : {}", pageable, elementNum);
                break;
            }
        }
        return result;
    }

    private LibraryNaruDetail convert(CrawlingResult crawling, int num) {
        return new LibraryNaruDetail(
                libraryListPageConvertor.findLibraryName(crawling, num).trim(),
                libraryListPageConvertor.findAddress(crawling, num).trim(),
                libraryListPageConvertor.findDetailNumber(crawling, num).trim());
    }
}
