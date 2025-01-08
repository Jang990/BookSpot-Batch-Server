package com.bookspot.batch.openapi.naru;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NaruApiUrlCreatorTest {

    @Mock
    NaruApiUrlHolder holder;

    @InjectMocks
    NaruApiUrlCreator creator;

    @Test
    void 페이징_파라미터_연결() {
        Mockito.when(holder.getLibraryUrl()).thenReturn("something-url");
        assertEquals("something-url?pageNo=0&pageSize=10",
                creator.buildLibraryApi(PageRequest.of(0, 10)));
    }

    @Test
    void 쿼리스트링_시작문자가_없다면_쿼리스트링을_시작() {
        Mockito.when(holder.getLibraryUrl()).thenReturn("something-url?something=1");
        assertEquals("something-url?something=1&pageNo=0&pageSize=10",
                creator.buildLibraryApi(PageRequest.of(0, 10)));
    }

}