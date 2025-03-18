package com.bookspot.batch.global.crawler.kdc;

import com.bookspot.batch.global.crawler.common.CrawlingResult;
import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import com.bookspot.batch.global.crawler.kdc.css.KdcCssTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KdcCrawler {
    public static final String KDC_WIKI_URL = "https://ko.wikipedia.org/wiki/%ED%95%9C%EA%B5%AD%EC%8B%AD%EC%A7%84%EB%B6%84%EB%A5%98%EB%B2%95";

    private final JsoupCrawler jsoupCrawler;
    private final KdcCodeParser kdcCodeParser;


    public List<KdcCode> findAll() {
        CrawlingResult crawlingResult = jsoupCrawler.get(KDC_WIKI_URL);

        KdcCssTarget cssTarget = new KdcCssTarget();
        List<KdcCode> result = new LinkedList<>();

        while (true) {
            KdcCode topCode = kdcCodeParser.parseTopCode(crawlingResult, cssTarget);
            if (topCode == null) {
                cssTarget.nextTop();
                continue;
            }

            result.add(topCode);
            result.addAll(findMidWithLeaf(crawlingResult, cssTarget, topCode));

            if(!cssTarget.hasNextTop())
                break;
            cssTarget.nextTop();
        }

        return result;
    }

    private List<KdcCode> findMidWithLeaf(CrawlingResult crawlingResult, KdcCssTarget cssTarget, KdcCode topCode) {
        List<KdcCode> result = new LinkedList<>();
        while (true) {
            KdcCode midCode = kdcCodeParser.parseMidCode(crawlingResult, cssTarget, topCode);

            if (midCode != null) {
                if(midCode.code() != topCode.code())
                    result.add(midCode);
                List<KdcCode> leafCodes = kdcCodeParser.parseLeafCodes(crawlingResult, cssTarget, midCode);
                result.addAll(leafCodes);
            }

            if(!cssTarget.hasNextMid())
                break;
            cssTarget.nextMid();
        }
        return result;
    }

}
