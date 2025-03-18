package com.bookspot.batch.global.crawler.kdc;

import com.bookspot.batch.global.crawler.common.CrawlingResult;
import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import com.bookspot.batch.global.crawler.common.exception.ElementNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KdcCrawler {
    private static final String KDC_WIKI_URL = "https://ko.wikipedia.org/wiki/%ED%95%9C%EA%B5%AD%EC%8B%AD%EC%A7%84%EB%B6%84%EB%A5%98%EB%B2%95";

    private static final String TABLE_SELECTOR_TEMPLATE = "#mw-content-text > div.mw-content-ltr.mw-parser-output > table:nth-child(%d)";
    private static final String MID_LEVEL_SELECTOR_TEMPLATE = TABLE_SELECTOR_TEMPLATE.concat("> tbody > tr:nth-child(%d) > td:nth-child(%d)");
    private static final String LEAF_SELECTOR_TEMPLATE = MID_LEVEL_SELECTOR_TEMPLATE.concat("> ul > li:nth-child(%d)");

    private final JsoupCrawler jsoupCrawler;
    private final KdcTextParser kdcTextParser;


    public List<KdcCode> findAll() {
        CrawlingResult crawlingResult = jsoupCrawler.get(KDC_WIKI_URL);

        /*for (int id = 0; id < 10; id++) {
            TABLE_SELECTOR_TEMPLATE.formatted(tableId(id));
        }*/

        String tableSelector = TABLE_SELECTOR_TEMPLATE.formatted(tableId(0));

        for (int midId = 1; midId <= 5; midId++) {
            // formatted 2,3이 아닌 4, 5 for문도 만들기.
            List<KdcCode> midCodeWithLeaf = findMidCodeWithLeaf(crawlingResult, tableId(0), 2, midId);
            System.out.println(midCodeWithLeaf);
        }

        return null;
    }

    private List<KdcCode> findMidCodeWithLeaf(CrawlingResult crawlingResult, int tableId, int line, int midId) {
        List<KdcCode> result = new LinkedList<>();
        String midLevelSelector = MID_LEVEL_SELECTOR_TEMPLATE.formatted(tableId, line, midId);
        KdcCode midCode = parseKdcCode(crawlingResult, midLevelSelector, null);
        result.add(midCode);

        for (int leafId = 1; leafId < 10; leafId++) {
            try {
                String leafSelector = LEAF_SELECTOR_TEMPLATE.formatted(tableId, line + 1, midId, leafId);
                result.add(parseKdcCode(crawlingResult, leafSelector, midCode));
            } catch (ElementNotFoundException e) {
                break;
            }
        }
        return result;
    }

    private KdcCode parseKdcCode(CrawlingResult crawlingResult, String cssSelector, @Nullable KdcCode parent) {
        return kdcTextParser.parse(
                crawlingResult.findElementText(cssSelector),
                parent
        );
    }

    public int tableId(int idx) {
        return 13 + idx * 3;
    }

}
