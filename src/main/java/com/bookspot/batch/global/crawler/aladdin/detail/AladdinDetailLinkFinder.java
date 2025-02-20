package com.bookspot.batch.global.crawler.aladdin.detail;

import com.bookspot.batch.global.crawler.common.CrawlingResult;
import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AladdinDetailLinkFinder {
    private final JsoupCrawler jsoupCrawler;

    public String find(String isbn) {
        CrawlingResult result = jsoupCrawler.get(DetailLinkConst.BOOK_SEARCH_URL + isbn);

        if(parseSearchCount(result) != 1)
            throw new IllegalArgumentException("검색 결과가 두 개 이상"); // TODO: 커스텀 예외 필요?

        return findBookDetailPageHref(result);
    }

    private String findBookDetailPageHref(CrawlingResult result) {
        return result.findElementAttribute(DetailLinkConst.PAGE_LINK.cssSelector(), "href");
    }

    private int parseSearchCount(CrawlingResult result) {
        return Integer.parseInt(result.findElementText(DetailLinkConst.SEARCH_RESULT_COUNT.cssSelector()));
    }
}
