package com.bookspot.batch.library.reader.file;

enum LibraryExcelSpec {
    NAME(0),     // 도서관명 - 2.28도서관
    ADDRESS(1),            // 주소 - 대구광역시 중구 2·28길 9
    TEL(2),       // 전화번호 - 053-257-2280
    FAX_NUMBER(3),         // 팩스번호 - 053-257-2284
    LATITUDE(4),           // 위도 - 35.8592504
    LONGITUDE(5),          // 경도 - 128.5894055
    HOMEPAGE(6),           // 홈페이지 - http://library.daegu.go.kr/228lib/index.do
    OPERATING_INFO(7),      // 운영시간 - 화~금 09:00~18:00, 토일 09:00~17:00
    CLOSED(8),            // 휴관일 - 매주 월요일 / 법정공휴일(일요일을 제외한 관공서 공휴일), 도서관 및 기타 사정으로 위탁기관장이 정하는 날
    LIBRARY_CODE(9);       // 도서관코드 - 127058

    public final int index;

    LibraryExcelSpec(int index) {
        this.index = index;
    }
}
