package com.bookspot.batch.global.openapi.naru;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NaruApiUrlCreator {
    private static final String PAGE_SIZE_OPTION_TEMPLATE = "pageNo=%d&pageSize=%d";
    private static final String QUERY_STRING_PREFIX = "?";
    private static final String PARAMETER_SEPARATOR = "&";

    private final NaruApiUrlHolder naruApiUrlHolder;

    public String buildLibraryApi(Pageable pageable) {
        StringBuilder builder = new StringBuilder(naruApiUrlHolder.getLibraryUrl());

        builder.append(
                getQuerySeparator(builder.toString()));
        return builder.append(
                PAGE_SIZE_OPTION_TEMPLATE.formatted(
                        pageable.getPageNumber(), pageable.getPageSize())).toString();
    }

    private String getQuerySeparator(String url) {
        if(url.contains(QUERY_STRING_PREFIX))
            return PARAMETER_SEPARATOR;
        return QUERY_STRING_PREFIX;
    }

}
