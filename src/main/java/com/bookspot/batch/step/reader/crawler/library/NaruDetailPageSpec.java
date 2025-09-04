package com.bookspot.batch.step.reader.crawler.library;

class NaruDetailPageSpec {
    protected static final String NAME_SELECTOR_TEMPLATE = "body > div > div > div:nth-child(%d) > a";
    protected static final String ADDRESS_SELECTOR_TEMPLATE = "body > div > div > div:nth-child(%d) > p";
    protected static final String DETAIL_SELECTOR_TEMPLATE = "body > div > div > div:nth-child(%d) > a";
    protected static final String DETAIL_ATTRIBUTE = "onclick";
    protected static final int ADDED_IDX = 1;
}

