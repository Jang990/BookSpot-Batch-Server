package com.bookspot.batch.global.crawler.kdc;

import com.bookspot.batch.global.crawler.common.CrawlingResult;
import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import com.bookspot.batch.global.crawler.common.exception.ElementNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KdcCrawler {
    private static final String KDC_WIKI_URL = "https://ko.wikipedia.org/wiki/%ED%95%9C%EA%B5%AD%EC%8B%AD%EC%A7%84%EB%B6%84%EB%A5%98%EB%B2%95";
    private static final String TABLE_SELECTOR_TEMPLATE = "#mw-content-text > div.mw-content-ltr.mw-parser-output > table:nth-child(%d)";

    private final JsoupCrawler jsoupCrawler;
    private final KdcTextParser kdcTextParser;


    public List<KdcCode> findAll() {
        CrawlingResult crawlingResult = jsoupCrawler.get(KDC_WIKI_URL);

        /*for (int id = 0; id < 10; id++) {
            TABLE_SELECTOR_TEMPLATE.formatted(tableId(id));
        }*/

        String tableSelector = TABLE_SELECTOR_TEMPLATE.formatted(tableId(0));

        for (int i = 1; i <= 5; i++) {
            // formatted 2,3이 아닌 4, 5 for문도 만들기.
            String midLevelSelector = tableSelector.concat("> tbody > tr:nth-child(%d) > td:nth-child(%d)".formatted(2, i));
            String leafLevelSelectorTemplate = tableSelector.concat("> tbody > tr:nth-child(%d) > td:nth-child(%d)".formatted(3, i))
                    .concat("> ul > li:nth-child(%d)");
            System.out.println();

            String midLevelText = crawlingResult.findElementText(midLevelSelector);
            KdcCode midCode = kdcTextParser.parse(midLevelText, null);
            System.out.println(midCode);

            for (int leafId = 1; leafId < 10; leafId++) {
                try {
                    String leafLevelText = crawlingResult.findElementText(leafLevelSelectorTemplate.formatted(leafId));
                    KdcCode leafCode = kdcTextParser.parse(leafLevelText, midCode);
                    System.out.println(leafCode);
                } catch (ElementNotFoundException e) {
                    break;
                }
            }

        }

        return null;
    }

    public int tableId(int idx) {
        return 13 + idx * 3;
    }

}
