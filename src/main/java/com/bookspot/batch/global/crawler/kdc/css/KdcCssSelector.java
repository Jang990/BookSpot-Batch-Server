package com.bookspot.batch.global.crawler.kdc.css;

public class KdcCssSelector {
    private static final String TABLE_SELECTOR_TEMPLATE = "#mw-content-text > div.mw-content-ltr.mw-parser-output > table:nth-child(%d)";
    private static final String TOP_LEVEL_SELECTOR_TEMPLATE = TABLE_SELECTOR_TEMPLATE.concat(" > tbody > tr:nth-child(2) > td:nth-child(1)");
    private static final String MID_LEVEL_SELECTOR_TEMPLATE = TABLE_SELECTOR_TEMPLATE.concat("> tbody > tr:nth-child(%d) > td:nth-child(%d)");
    private static final String LEAF_SELECTOR_TEMPLATE = MID_LEVEL_SELECTOR_TEMPLATE.concat("> ul > li:nth-child(%d)");

    public static String generateTopLevel(KdcCssTarget target) {
        return TOP_LEVEL_SELECTOR_TEMPLATE.formatted(target.top());
    }

    public static String generateMidLevel(KdcCssTarget target) {
        return MID_LEVEL_SELECTOR_TEMPLATE.formatted(target.top(), target.midLine(), target.mid());
    }

    public static String generateLeafLevel(KdcCssTarget target) {
        return LEAF_SELECTOR_TEMPLATE.formatted(target.top(), target.leafLine(), target.mid(), target.leaf());
    }
}
