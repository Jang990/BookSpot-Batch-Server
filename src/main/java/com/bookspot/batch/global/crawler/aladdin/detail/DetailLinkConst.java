package com.bookspot.batch.global.crawler.aladdin.detail;

public enum DetailLinkConst {
    SEARCH_RESULT_COUNT("#keyword_wrap > table > tbody > tr > td:nth-child(3) > div.ss_line5 > table > tbody > tr > td > div > span.ss_f_g_l"),
    PAGE_LINK("#Search3_Result > div > table > tbody > tr > td:nth-child(3) > table > tbody > tr:nth-child(1) > td:nth-child(1) > div:nth-child(1) > ul > li:nth-child(1) > a.bo3");

    protected static final String BOOK_SEARCH_URL = "https://www.aladin.co.kr/search/wsearchresult.aspx?SearchTarget=All&SearchWord=";
    private final String cssSelector;
    DetailLinkConst(String selector) {
        this.cssSelector = selector;
    }

    public String cssSelector() {
        return cssSelector;
    }
}
