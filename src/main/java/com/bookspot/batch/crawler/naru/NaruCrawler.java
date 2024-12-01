package com.bookspot.batch.crawler.naru;

import com.bookspot.batch.crawler.common.CookieKeyConst;
import com.bookspot.batch.crawler.common.JsoupCrawler;
import com.bookspot.batch.crawler.common.CrawlingResult;
import com.bookspot.batch.crawler.common.RequestData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class NaruCrawler {
    private static final String DOMAIN = "https://data4library.kr";
    private static final String LIBRARY_LIST_PATH = DOMAIN.concat("/openDataL");
    private static final String LIBRARY_DETAIL_PATH = DOMAIN.concat("/openDataV");

    private final String CSS_QUERY_DATE_TEXT = "tr td:nth-child(3)";

    private static final String CSS_QUERY_CSRF_TOKEN = "input[name=_csrf]";
    private static final String VALUE_CSRF_TOKEN = "value";

    private final String CSS_QUERY_CSV_FILE_LINK = "a.download_link:nth-child(1)";
    private static final String VALUE_DOWNLOAD_LINK = "data-url";


    private final JsoupCrawler crawler;

    public NaruRequest createRequest(LibraryCode code) {
        CrawlingResult result = crawler.get(LIBRARY_LIST_PATH);

        return new NaruRequest(
                result.getCookie(CookieKeyConst.SESSION_ID),
                result.findElementAttribute(
                        CSS_QUERY_CSRF_TOKEN, VALUE_CSRF_TOKEN),
                code.getCode()
        );
    }

    public CsvFilePath findCurrentBooksFilePath(NaruRequest request) {
        RequestData requestData = new RequestData(
                LIBRARY_DETAIL_PATH,
                request.getHeader(),
                request.getRequestBody()
        );

        CrawlingResult result = crawler.post(requestData);

        return new CsvFilePath(
                DOMAIN.concat(findCurrentCsvFileLink(result)),
                LocalDate.parse(result.findElementText(CSS_QUERY_DATE_TEXT))
        );
    }

    private String findCurrentCsvFileLink(CrawlingResult result) {
        return result.findElementAttribute(CSS_QUERY_CSV_FILE_LINK, VALUE_DOWNLOAD_LINK);
    }
}
