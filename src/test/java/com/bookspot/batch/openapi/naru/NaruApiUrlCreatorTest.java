package com.bookspot.batch.openapi.naru;

import com.bookspot.batch.data.document.RankingAge;
import com.bookspot.batch.data.document.RankingGender;
import com.bookspot.batch.data.document.RankingType;
import com.bookspot.batch.global.openapi.naru.NaruApiUrlCreator;
import com.bookspot.batch.global.openapi.naru.NaruApiUrlHolder;
import com.bookspot.batch.step.reader.api.top50.RankingConditions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NaruApiUrlCreatorTest {

    @Mock NaruApiUrlHolder holder;
    @InjectMocks NaruApiUrlCreator creator;

    private String testApiUrl = "something-url?my-key=abc";

    @Test
    void 페이징_파라미터_연결() {
        Mockito.when(holder.getLibraryUrl()).thenReturn(testApiUrl);
        assertEquals(testApiUrl.concat("&pageNo=0&pageSize=10"),
                creator.buildLibraryApi(PageRequest.of(0, 10)));
    }

    @Test
    void 쿼리스트링_시작_물음표가_없다면_시작_물음표를_붙이고_쿼리스트링_시작() {
        String testUrl = "something-url"; // 끝에 ?가 없음
        Mockito.when(holder.getLibraryUrl()).thenReturn(testUrl);
        assertEquals(
                testUrl.concat("?pageNo=0&pageSize=10"),
                creator.buildLibraryApi(PageRequest.of(0, 10))
        );

        Mockito.when(holder.getTop50Books()).thenReturn(testUrl);
        assertEquals(
                testUrl.concat("?&startDt=2025-08-18&endDt=2025-08-24"),
                creator.buildWeeklyTop50Api(
                        LocalDate.of(2025, 8, 18),
                        new RankingConditions(RankingType.WEEKLY, RankingGender.ALL, RankingAge.ALL)
                )
        );
    }

    @Test
    void 기준일이_포함된_월to일_기간_api_생성() {
        Mockito.when(holder.getTop50Books()).thenReturn(testApiUrl);
        assertEquals(
                testApiUrl.concat("&startDt=2025-08-18&endDt=2025-08-24"),
                creator.buildWeeklyTop50Api(
                        LocalDate.of(2025, 8, 18),
                        new RankingConditions(RankingType.WEEKLY, RankingGender.ALL, RankingAge.ALL)
                )
        );
    }

    @Test
    void 기준일의_달의_기간_api_생성() {
        Mockito.when(holder.getTop50Books()).thenReturn(testApiUrl);
        assertEquals(
                testApiUrl.concat("&startDt=2025-08-01&endDt=2025-08-31"),
                creator.buildWeeklyTop50Api(
                        LocalDate.of(2025, 8, 18),
                        new RankingConditions(RankingType.MONTHLY, RankingGender.ALL, RankingAge.ALL)
                )
        );
    }

}