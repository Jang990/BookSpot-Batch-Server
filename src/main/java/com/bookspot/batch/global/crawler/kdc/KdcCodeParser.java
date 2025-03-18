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
    private static final String unused_code_text = "[미사용]";
    private final KdcTextParser kdcTextParser;

    protected KdcCode parseTopCode(CrawlingResult crawlingResult, KdcCssTarget cssTarget) {
        String topLevelSelector = KdcCssSelector.generateTopLevel(cssTarget);
        return parseKdcCode(crawlingResult, topLevelSelector, null);
    }

    protected KdcCode parseMidCode(CrawlingResult crawlingResult, KdcCssTarget cssTarget, KdcCode topCode) {
        String midLevelSelector = KdcCssSelector.generateMidLevel(cssTarget);
        return parseKdcCode(crawlingResult, midLevelSelector, topCode);
    }

    protected List<KdcCode> parseLeafCodes(CrawlingResult crawlingResult, KdcCssTarget cssTarget, KdcCode midCode) {
        List<KdcCode> result = new LinkedList<>();

        while (true) {
            try {
                String leafSelector = KdcCssSelector.generateLeafLevel(cssTarget);
                KdcCode kdcCode = parseKdcCode(crawlingResult, leafSelector, midCode);
                if (kdcCode != null)
                    result.add(kdcCode);
                if(!cssTarget.hasNextLeaf())
                    break;
                cssTarget.nextLeaf();
            } catch (ElementNotFoundException e) {
                break;
            }
        }

        return result;
    }

    private @Nullable KdcCode parseKdcCode(CrawlingResult crawlingResult, String cssSelector, @Nullable KdcCode parent) {
        KdcCode result = kdcTextParser.parse(
                crawlingResult.findElementText(cssSelector),
                parent
        );
        if(unused_code_text.equals(result.name()))
            return null;
        return result;
    }
}
