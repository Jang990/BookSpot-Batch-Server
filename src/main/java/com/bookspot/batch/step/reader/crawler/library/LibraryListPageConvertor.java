package com.bookspot.batch.step.reader.crawler.library;

import com.bookspot.batch.global.crawler.common.CrawlingResult;
import org.springframework.stereotype.Service;

@Service
class LibraryListPageConvertor {
    public String findAddress(CrawlingResult crawling, int num) {
        return crawling.findElementText(addressSelector(num));
    }

    public String findLibraryName(CrawlingResult crawling, int num) {
        return crawling.findElementText(nameSelector(num));
    }

    public String findDetailNumber(CrawlingResult crawling, int num) {
        return removeSurrounding(crawling.findElementAttribute(detailSelector(num), NaruDetailPageSpec.DETAIL_ATTRIBUTE));
    }

    private String detailSelector(int num) {
        return NaruDetailPageSpec.DETAIL_SELECTOR_TEMPLATE.formatted(num + NaruDetailPageSpec.ADDED_IDX);
    }

    private String addressSelector(int num) {
        return NaruDetailPageSpec.ADDRESS_SELECTOR_TEMPLATE.formatted(num + NaruDetailPageSpec.ADDED_IDX);
    }

    private String nameSelector(int num) {
        return NaruDetailPageSpec.NAME_SELECTOR_TEMPLATE.formatted(num + NaruDetailPageSpec.ADDED_IDX);
    }

    // detailView('8517'); -> 8517
    private String removeSurrounding(String detailAttribute) {
        return detailAttribute.substring(12, detailAttribute.length() - 3);
    }
}
