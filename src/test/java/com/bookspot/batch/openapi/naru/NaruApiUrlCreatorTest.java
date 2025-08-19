package com.bookspot.batch.openapi.naru;

import com.bookspot.batch.global.openapi.naru.NaruApiUrlCreator;
import com.bookspot.batch.global.openapi.naru.NaruApiUrlHolder;
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

        Mockito.when(holder.getTrendUrl()).thenReturn("something-url-trend?something=1");
        assertEquals(
                "something-url-trend?something=1&searchDt=2025-01-01",
                creator.buildTrendApi(LocalDate.of(2025, 1, 1))
        );
    }

    @Test
    void 트렌드_api_생성() {
        Mockito.when(holder.getTrendUrl()).thenReturn("something-url");
        assertEquals(
                "something-url?searchDt=2025-08-19",
                creator.buildTrendApi(LocalDate.of(2025, 8, 19))
        );
    }

}