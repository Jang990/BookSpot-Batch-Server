package com.bookspot.batch.global.crawler.aladdin;

import com.bookspot.batch.global.crawler.common.CrawlingResult;
import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AladdinCrawler {
    private final JsoupCrawler jsoupCrawler;

    public AladdinBookDetail findBookDetail(String isbn) {
        String itemId = "60550259";
        CrawlingResult result = jsoupCrawler.get(BookDetailConst.BOOK_DETAIL_URL + itemId);

        AladdinBookDetail detail = new AladdinBookDetail(
                itemId,
                isbn,
                result.findElementAttribute(BookDetailConst.IMAGE.cssSelector(), "src"),
                result.findElementText(BookDetailConst.TITLE.cssSelector()),
                result.findElementText(BookDetailConst.SUB_TITLE.cssSelector()),
                result.findElementText(BookDetailConst.AUTHOR.cssSelector()),
                result.findElementText(BookDetailConst.PUBLISHER.cssSelector()),
                null, // TODO: 자세한 정보 파싱 필요
                null,
                parsePublishedDate(result),
                parsePageCount(result)
        );

        return detail;
    }

    private LocalDate parsePublishedDate(CrawlingResult result) {
        String bookMetadata = result.findElementText(BookDetailConst.BOOK_METADATA.cssSelector());
        return LocalDate.parse(
                bookMetadata.substring(bookMetadata.length() - 10),
                DateTimeFormatter.ISO_DATE);
    }

    private int parsePageCount(CrawlingResult result) {
        String pageCountStr = result.findElementText((BookDetailConst.PAGE_COUNT.cssSelector()));
        return Integer.parseInt(pageCountStr.substring(0, pageCountStr.length() -1));
    }
}
