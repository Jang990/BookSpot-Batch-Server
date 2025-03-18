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
        List<KdcCode> result = new LinkedList<>();

        result.addAll(kdcCodeParser.parseTopCodes(crawlingResult, new KdcCssTarget()));
        result.addAll(findMidCodes(crawlingResult));
        result.addAll(findLeafCodes(crawlingResult));
        return result;
    }

    private List<KdcCode> findMidCodes(CrawlingResult crawlingResult) {
        List<KdcCode> result = new LinkedList<>();
        KdcCssTarget cssTarget = new KdcCssTarget();
        while (true) {
            List<KdcCode> midCodes = kdcCodeParser.parseMidCodes(crawlingResult, cssTarget);
            result.addAll(midCodes);
            if(!cssTarget.hasNextTop())
                break;
            cssTarget.nextTop();
        }
        return result;
    }

    private List<KdcCode> findLeafCodes(CrawlingResult crawlingResult) {
        List<KdcCode> result = new LinkedList<>();
        KdcCssTarget cssTarget = new KdcCssTarget();
        while (true) {
            result.addAll(findLeafCodesInMidLevel(crawlingResult, cssTarget));

            if(!cssTarget.hasNextTop())
                break;
            cssTarget.nextTop();
        }
        return result;
    }

    private List<KdcCode> findLeafCodesInMidLevel(CrawlingResult crawlingResult, KdcCssTarget cssTarget) {
        List<KdcCode> result = new LinkedList<>();
        while (true) {
            List<KdcCode> leafCodes = kdcCodeParser.parseLeafCodes(crawlingResult, cssTarget);
            result.addAll(leafCodes);
            if(!cssTarget.hasNextMid())
                break;
            cssTarget.nextMid();
        }
        return result;
    }

}
