package com.bookspot.batch.book.reader;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class BookCsvData {
    private String controlNumber;       // CTRL_NO - 제어번호 - 727130663
    private String authorName;          // AUTHR_NM - 저작자명 - 이광웅
    private String volumeName;          // VLM_NM - 권명 - 9
    private String publicationYear;     // PBLICTE_YEAR - 발행년도 - 2009
    private String classificationNumber; // CL_SMBL_NO - 분류기호번호 - 517.16
    private String bookSymbolNumber;    // BOOK_SMBL_NO - 서적기호번호 - ㅂ736ㄸ
    private String titleName;           // TITLE_NM - 제목명 - (Why)지구
    private String libraryCode;            // LBRRY_CD - 도서관코드 - 6201
    private String isbn13Number;        // ISBN_THIRTEEN_NO - ISBN13번호 - 9788930202350
    private String representativeBook;    // REPRSNT_BOOK_AT - 대표서적여부 - Y/N
    private String registerNumber;      // REGIST_NO - 등록번호 - EM0000120399
    private String incomeFlagName;      // INCME_FLAG_NM - 수입구분명 - 2
    private String manageFlagName;      // MANAGE_FLAG_NM - 관리구분명 - AB
    private String mediaFlagName;       // MEDIA_FLAG_NM - 미디어구분명 - PR
    private String utilizationLimitFlagName; // UTILIIZA_LMTT_FLAG_NM - 이용제한구분명 - GM
    private String utilizationTargetFlagName; // UTILIIZA_TRGET_FLAG_NM - 이용대상구분명 - PU
    private String accompanyDataName;   // ACP_DATA_NM - 동반자료명 - 카드 1매 + 쪽번호표 1책 + 정오표 1매 or EMPTY
    private String singleVolumeIsbn;    // SGVL_ISBN_NO - 낱권ISBN번호 - ISBN13과 동일하거나 EMPTY
    private String singleVolumeIsbnAdditionalSymbolName; // SGVL_ISBN_ADTION_SMBL_NM - 낱권ISBN부가기호명 - EMPTY
    private String classificationSymbolFlagName; // CL_SMBL_FLAG_NM - 분류기호구분명 - 1
    private String volumeSymbolName;    // VLM_SMBL_NM - 권기호명 - v.2
    private String duplicateCopySymbolName; // DUCACPY_SMBL_NM - 복사본기호명 - 3
    private LocalDate registerDate; // REGIST_DE - 등록일자 - 2024-09-27  12:00:00 AM
    private String isbn13OriginalNumber; // ISBN_THIRTEEN_ORGT_NO - ISBN13원문번호 - ISBN13과 동일하거나 EMPTY
    private String masterLibraryCode;      // MASTR_LBRRY_CD - 마스터도서관코드 - 6200
    private boolean volumeExists;          // VLM_EXST_AT - 권존재여부 - Y/N
    private String setIsbnChanged;        // SET_ISBN_CHG_AT - 집합ISBN변경여부 - Y/N
    private String volumeOriginalName;  // VLM_ORGT_NM - 권원문명 - EMPTY
    private String titleSubstituteName; // TITLE_SBST_NM - 제목대체명 - why지구
    private String kdcName;             // KDC_NM - KDC명 - classificationNumber 앞 숫자와 동일
    private String bookClassificationCode; // BOOK_CL_CD - 서적분류코드 - 아동
    private String bookLocationCode;    // BOOK_LC_CD - 서적위치코드 - B15

    public void setVolumeExists(String volumeExists) {
        if (!volumeExists.equals("Y") && !volumeExists.equals("N"))
            throw new IllegalArgumentException("representativeBook 필드 오류 - %s".formatted(representativeBook));
        this.volumeExists = volumeExists.equals("Y");
    }
}
