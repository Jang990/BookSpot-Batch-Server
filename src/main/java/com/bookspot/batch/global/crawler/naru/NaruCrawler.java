package com.bookspot.batch.global.crawler.naru;

import com.bookspot.batch.global.crawler.common.CookieKeyConst;
import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import com.bookspot.batch.global.crawler.common.CrawlingResult;
import com.bookspot.batch.global.crawler.common.RequestData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class NaruCrawler {
    private static final String DOMAIN = "https://data4library.kr";
    private static final String LIBRARY_LIST_PATH = DOMAIN.concat("/openDataL");
    private static final String LIBRARY_DETAIL_PATH = DOMAIN.concat("/openDataV");


    private final String CSS_QUERY_DATE_TEXT = "body > div.sub_contents_wrap > div > div:nth-child(3) > div:nth-child(2) > div.l_c_info > ul > li";

    private static final String CSS_QUERY_CSRF_TOKEN = "input[name=_csrf]";
    private static final String VALUE_CSRF_TOKEN = "value";


    private final String CSS_QUERY_CSV_FILE_LINK = "a.download_link:nth-child(1)";
//    private final String CSS_QUERY_CSV_FILE_LINK = "#sb-site > section > div.sub_container > div:nth-child(5) > table > tbody > tr:nth-child(2) > td.data_type.br_none > a"; // 이전달 css
    private static final String VALUE_DOWNLOAD_LINK = "data-url";


    private final JsoupCrawler crawler;

    public NaruDetailRequest createRequest(String code) {
        CrawlingResult result = crawler.get(LIBRARY_LIST_PATH);

        return new NaruDetailRequest(
                result.getCookie(CookieKeyConst.SESSION_ID),
                result.findElementAttribute(
                        CSS_QUERY_CSRF_TOKEN, VALUE_CSRF_TOKEN),
                code
        );
    }

    public CsvFilePath findCurrentBooksFilePath(NaruDetailRequest request) {
        RequestData requestData = new RequestData(
                LIBRARY_DETAIL_PATH,
                request.getHeader(),
                request.getRequestBody()
        );

        CrawlingResult result = crawler.post(requestData);

        return new CsvFilePath(
                DOMAIN.concat(findCurrentCsvFileLink(result)),
                LocalDate.parse(result.findElementText(CSS_QUERY_DATE_TEXT).replace("데이터 제공일", "").trim())
        );
    }

    private String findCurrentCsvFileLink(CrawlingResult result) {
        return result.findElementAttribute(CSS_QUERY_CSV_FILE_LINK, VALUE_DOWNLOAD_LINK);
    }
}
