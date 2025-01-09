package com.bookspot.batch.openapi;

import com.bookspot.batch.library.data.Library;
import com.bookspot.batch.library.api.LibraryApiRequester;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LibraryApiRequesterTest {

    @Autowired
    LibraryApiRequester requester;

//    @Test
    void 정보나루_도서관_API_테스트() {
        PageRequest pageable = PageRequest.of(0, 10);
        List<Library> result = requester.findAllSupportedLibrary(pageable);
        for (Library library : result) {
            System.out.println(library.getName());
        }
    }

//    @Test
    void 도서관_정보가_없다면_EMPTY_LIST_반환() {
        PageRequest pageable = PageRequest.of(17, 100);
        List<Library> result = requester.findAllSupportedLibrary(pageable);
        assertTrue(result.isEmpty());
    }

}