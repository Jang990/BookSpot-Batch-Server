package com.bookspot.batch.global.crawler.aladdin;

public enum BookDetailConst {
    TITLE("#Ere_prod_allwrap > div.Ere_prod_topwrap > div.Ere_prod_titlewrap > div.left > div > ul > li:nth-child(2) > div > span.Ere_bo_title"),
    SUB_TITLE("#Ere_prod_allwrap > div.Ere_prod_topwrap > div.Ere_prod_titlewrap > div.left > div > ul > li:nth-child(2) > div > span.Ere_sub1_title"),
    IMAGE("#CoverMainImage"),
    AUTHOR("#Ere_prod_allwrap > div.Ere_prod_topwrap > div.Ere_prod_titlewrap > div.left > div > ul > li.Ere_sub2_title > a:nth-child(1)"),
    PUBLISHER("#Ere_prod_allwrap > div.Ere_prod_topwrap > div.Ere_prod_titlewrap > div.left > div > ul > li.Ere_sub2_title > a:nth-child(3)"),
    PAGE_COUNT("#Ere_prod_allwrap > div.Ere_prod_middlewrap > div:nth-child(1) > div.Ere_prod_mconts_R > div.conts_info_list1 > ul > li:nth-child(1)"),
    BOOK_METADATA("#Ere_prod_allwrap > div.Ere_prod_topwrap > div.Ere_prod_titlewrap > div.left > div > ul > li.Ere_sub2_title");

    protected static final String BOOK_DETAIL_URL = "https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=";
    private final String cssSelector;
    BookDetailConst(String selector) {
        this.cssSelector = selector;
    }

    public String cssSelector() {
        return cssSelector;
    }
}
