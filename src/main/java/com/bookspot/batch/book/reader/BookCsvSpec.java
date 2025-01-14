package com.bookspot.batch.book.reader;

public enum BookCsvSpec {
    CONTROL_NUMBER("controlNumber"), // CTRL_NO - 제어번호 - 727130663
    AUTHOR_NAME("authorName"), // AUTHR_NM - 저작자명 - 이광웅
    VOLUME_NAME("volumeName"), // VLM_NM - 권명 - 9
    PUBLICATION_YEAR("publicationYear"), // PBLICTE_YEAR - 발행년도 - 2009
    CLASSIFICATION_NUMBER("classificationNumber"), // CL_SMBL_NO - 분류기호번호 - 517.16
    BOOK_SYMBOL_NUMBER("bookSymbolNumber"), // BOOK_SMBL_NO - 서적기호번호 - ㅂ736ㄸ
    TITLE_NAME("titleName"), // TITLE_NM - 제목명 - (Why)지구
    LIBRARY_CODE("libraryCode"), // LBRRY_CD - 도서관코드 - 6201
    ISBN13_NUMBER("isbn13Number"), // ISBN_THIRTEEN_NO - ISBN13번호 - 9788930202350
    REPRESENTATIVE_BOOK("representativeBook"), // REPRSNT_BOOK_AT - 대표서적여부 - Y/N
    REGISTER_NUMBER("registerNumber"), // REGIST_NO - 등록번호 - EM0000120399
    INCOME_FLAG_NAME("incomeFlagName"), // INCME_FLAG_NM - 수입구분명 - 2
    MANAGE_FLAG_NAME("manageFlagName"), // MANAGE_FLAG_NM - 관리구분명 - AB
    MEDIA_FLAG_NAME("mediaFlagName"), // MEDIA_FLAG_NM - 미디어구분명 - PR
    UTILIZATION_LIMIT_FLAG_NAME("utilizationLimitFlagName"), // UTILIIZA_LMTT_FLAG_NM - 이용제한구분명 - GM
    UTILIZATION_TARGET_FLAG_NAME("utilizationTargetFlagName"), // UTILIIZA_TRGET_FLAG_NM - 이용대상구분명 - PU
    ACCOMPANY_DATA_NAME("accompanyDataName"), // ACP_DATA_NM - 동반자료명 - 카드 1매 + 쪽번호표 1책 + 정오표 1매 or EMPTY
    SINGLE_VOLUME_ISBN("singleVolumeIsbn"), // SGVL_ISBN_NO - 낱권ISBN번호 - ISBN13과 동일하거나 EMPTY
    SINGLE_VOLUME_ISBN_ADDITIONAL_SYMBOL_NAME("singleVolumeIsbnAdditionalSymbolName"), // SGVL_ISBN_ADTION_SMBL_NM - 낱권ISBN부가기호명 - EMPTY
    CLASSIFICATION_SYMBOL_FLAG_NAME("classificationSymbolFlagName"), // CL_SMBL_FLAG_NM - 분류기호구분명 - 1
    VOLUME_SYMBOL_NAME("volumeSymbolName"), // VLM_SMBL_NM - 권기호명 - v.2
    DUPLICATE_COPY_SYMBOL_NAME("duplicateCopySymbolName"), // DUCACPY_SMBL_NM - 복사본기호명 - 3
    REGISTER_DATE("registerDate"), // REGIST_DE - 등록일자 - 2024-09-27  12:00:00 AM
    ISBN13_ORIGINAL_NUMBER("isbn13OriginalNumber"), // ISBN_THIRTEEN_ORGT_NO - ISBN13원문번호 - ISBN13과 동일하거나 EMPTY
    MASTER_LIBRARY_CODE("masterLibraryCode"), // MASTR_LBRRY_CD - 마스터도서관코드 - 6200
    VOLUME_EXISTS("volumeExists"), // VLM_EXST_AT - 권존재여부 - Y/N
    SET_ISBN_CHANGED("setIsbnChanged"), // SET_ISBN_CHG_AT - 집합ISBN변경여부 - Y/N
    VOLUME_ORIGINAL_NAME("volumeOriginalName"), // VLM_ORGT_NM - 권원문명 - EMPTY
    TITLE_SUBSTITUTE_NAME("titleSubstituteName"), // TITLE_SBST_NM - 제목대체명 - why지구
    KDC_NAME("kdcName"), // KDC_NM - KDC명 - classificationNumber 앞 숫자와 동일
    BOOK_CLASSIFICATION_CODE("bookClassificationCode"), // BOOK_CL_CD - 서적분류코드 - 아동
    BOOK_LOCATION_CODE("bookLocationCode"); // BOOK_LC_CD - 서적위치코드 - B15

    private final String fieldName;

    BookCsvSpec(String fieldName) {
        this.fieldName = fieldName;
    }

    public String value() {
        return fieldName;
    }
}
