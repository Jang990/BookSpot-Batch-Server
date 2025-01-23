package com.bookspot.batch.step.reader.crawler.library;

class NaruDetailPageSpec {
    protected static final String NAME_SELECTOR_TEMPLATE = "#pagef > div.tbl_scroll_box.mgb_20 > table > tbody > tr:nth-child(%d) > td:nth-child(2) > a";
    protected static final String ADDRESS_SELECTOR_TEMPLATE = "#pagef > div.tbl_scroll_box.mgb_20 > table > tbody > tr:nth-child(%d) > td.align_left.br_none";
    protected static final String DETAIL_SELECTOR_TEMPLATE = "#pagef > div.tbl_scroll_box.mgb_20 > table > tbody > tr:nth-child(%d) > td:nth-child(2) > a";
    protected static final String DETAIL_ATTRIBUTE = "onclick";
}
