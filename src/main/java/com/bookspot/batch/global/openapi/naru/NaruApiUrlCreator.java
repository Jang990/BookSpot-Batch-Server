package com.bookspot.batch.global.openapi.naru;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class NaruApiUrlCreator {
    private static final String PAGE_SIZE_OPTION_TEMPLATE = "pageNo=%d&pageSize=%d";
    private static final String QUERY_STRING_PREFIX = "?";
    private static final String PARAMETER_SEPARATOR = "&";

    private final NaruApiUrlHolder naruApiUrlHolder;

    public String buildLibraryApi(Pageable pageable) {
        return naruApiUrlHolder.getLibraryUrl()
                + getQuerySeparator(naruApiUrlHolder.getLibraryUrl())
                + toPageQuery(pageable);
    }

    public String buildWeeklyTop50Api(LocalDate baseDate) {
        LocalDate start = baseDate.minusDays(7);
        LocalDate end = baseDate.minusDays(1);

        return naruApiUrlHolder.getWeeklyTop50Url() +
                getQuerySeparator(naruApiUrlHolder.getWeeklyTop50Url()) +
                "startDt=".concat(start.toString()) +
                PARAMETER_SEPARATOR + "endDt=".concat(end.toString());
    }

    private String toPageQuery(Pageable pageable) {
        return PAGE_SIZE_OPTION_TEMPLATE.formatted(
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }

    private String getQuerySeparator(String url) {
        if(url.contains(QUERY_STRING_PREFIX))
            return PARAMETER_SEPARATOR;
        return QUERY_STRING_PREFIX;
    }

}
