package com.bookspot.batch.step.reader.api.library;

import lombok.Data;

import java.util.List;

/*
{
    "response": {
        "request": {
            "pageNo": "1",
            "pageSize": "10"
        },
        "pageNo": 1,
        "pageSize": 10,
        "numFound": 1533,
        "resultNum": 10,
        "libs": [
            {
                "lib": {
                    "libCode": "127058",
                    "libName": "2.28도서관",
                    "address": "대구광역시 중구 2·28길 9",
                    "tel": "053-257-2280",
                    "fax": "053-257-2284",
                    "latitude": "35.8592504",
                    "longitude": "128.5894055",
                    "homepage": "http://library.daegu.go.kr/228lib/index.do",
                    "closed": "매주 월요일 / 법정공휴일(일요일을 제외한 관공서 공휴일), 도서관 및 기타 사정으로 위탁기관장이 정하는 날",
                    "operatingTime": "화~금 09:00~18:00, 토일 09:00~17:00",
                    "BookCount": "57391"
                }
            },
            {
                "lib": {
                    "libCode": "711618",
                    "libName": "KB국민은행과 함께하는 나무 작은도서관",
                    "address": "서울특별시 노원구 동일로 1405",
                    "tel": "070-4158-9660",
                    "fax": "-",
                    "latitude": "37.6538195",
                    "longitude": "127.0602181",
                    "homepage": "https://www.nowonlib.kr/",
                    "closed": "매주 토요일, 일요일 / 법정공휴일",
                    "operatingTime": "평일 09:00 ~ 18:00",
                    "BookCount": "3585"
                }
            }, ...
        ]
}
 */
@Data
class SupportedLibraryResponseSpec {
    private Response response;

    @Data
    public static class Response {
        private int numFound;
        private List<LibWrapper> libs;

        @Data
        public static class LibWrapper {
            private LibraryInfoSpec lib;
        }

        @Data
        public static class LibraryInfoSpec {
            private String libCode;
            private String libName;
            private String address;
            private String tel;
            private String fax;
            private String latitude;
            private String longitude;
            private String homepage;
            private String closed;
            private String operatingTime;
            private String BookCount;
        }
    }
}
