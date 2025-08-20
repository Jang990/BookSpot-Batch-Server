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

    @Test
    void 페이징_파라미터_연결() {
        Mockito.when(holder.getLibraryUrl()).thenReturn("something-url");
        assertEquals("something-url?pageNo=0&pageSize=10",
                creator.buildLibraryApi(PageRequest.of(0, 10)));
    }

    @Test
    void 쿼리스트링_시작문자가_없다면_쿼리스트링을_시작() {
        Mockito.when(holder.getLibraryUrl()).thenReturn("something-url-library?something=1");
        assertEquals(
                "something-url-library?something=1&pageNo=0&pageSize=10",
                creator.buildLibraryApi(PageRequest.of(0, 10))
        );

        Mockito.when(holder.getWeeklyTop50Url()).thenReturn("something-url-top100?something=1");
        assertEquals(
                "something-url-top100?something=1&startDt=2025-08-11&endDt=2025-08-17",
                creator.buildWeeklyTop50Api(
                        LocalDate.of(2025, 8, 18),
                        new RankingConditions(RankingType.MONTHLY, RankingGender.ALL, RankingAge.ALL)
                )
        );
    }

    @Test
    void 이전주_대출수_TOP_100_api_생성() {
        Mockito.when(holder.getWeeklyTop50Url()).thenReturn("something-url");
        assertEquals(
                "something-url?startDt=2025-08-11&endDt=2025-08-17",
                creator.buildWeeklyTop50Api(
                        LocalDate.of(2025, 8, 18),
                        new RankingConditions(RankingType.MONTHLY, RankingGender.ALL, RankingAge.ALL)
                )
        );
    }

}