package com.bookspot.batch.global.crawler.kdc;

import com.bookspot.batch.global.crawler.common.CrawlingResult;
import com.bookspot.batch.global.crawler.common.exception.ElementNotFoundException;
import com.bookspot.batch.global.crawler.kdc.css.KdcCssSelector;
import com.bookspot.batch.global.crawler.kdc.css.KdcCssTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KdcCodeParser {
    private static final String UNUSED_CODE_TEXT = "[미사용]";
    private final KdcTextParser kdcTextParser;

    protected List<KdcCode> parseTopCodes(CrawlingResult crawlingResult, KdcCssTarget cssTarget) {
        List<KdcCode> result = new LinkedList<>();
        while (true) {
            KdcCode topCode = parseTopCode(crawlingResult, cssTarget);
            if (topCode == null) {
                cssTarget.nextTop();
                continue;
            }

            result.add(topCode);
            if(!cssTarget.hasNextTop())
                break;
            cssTarget.nextTop();
        }

        return result;
    }

    protected List<KdcCode> parseMidCodes(CrawlingResult crawlingResult, KdcCssTarget cssTarget) {
        List<KdcCode> result = new LinkedList<>();
        while (true) {
            KdcCode midCode = parseMidCode(crawlingResult, cssTarget);

            if (midCode != null && midCode.code() % 100 != 0)
                result.add(midCode);

            if(!cssTarget.hasNextMid())
                break;
            cssTarget.nextMid();
        }
        return result;
    }

    protected List<KdcCode> parseLeafCodes(CrawlingResult crawlingResult, KdcCssTarget cssTarget) {
        List<KdcCode> result = new LinkedList<>();

        while (true) {
            try {
                String leafSelector = KdcCssSelector.generateLeafLevel(cssTarget);
                KdcCode leafCode = parseKdcCode(crawlingResult, leafSelector);
                if (leafCode != null)
                    result.add(leafCode);
                if(!cssTarget.hasNextLeaf())
                    break;
                cssTarget.nextLeaf();
            } catch (ElementNotFoundException e) {
                break;
            }
        }

        return result;
    }

    private KdcCode parseTopCode(CrawlingResult crawlingResult, KdcCssTarget cssTarget) {
        String topLevelSelector = KdcCssSelector.generateTopLevel(cssTarget);
        return parseKdcCode(crawlingResult, topLevelSelector);
    }

    private KdcCode parseMidCode(CrawlingResult crawlingResult, KdcCssTarget cssTarget) {
        String midLevelSelector = KdcCssSelector.generateMidLevel(cssTarget);
        return parseKdcCode(crawlingResult, midLevelSelector);
    }

    private @Nullable KdcCode parseKdcCode(CrawlingResult crawlingResult, String cssSelector) {
        KdcCode result = kdcTextParser.parse(crawlingResult.findElementText(cssSelector));

        if(UNUSED_CODE_TEXT.equals(result.name()))
            return null;
        return result;
    }
}
