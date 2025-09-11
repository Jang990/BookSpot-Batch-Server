package com.bookspot.batch.step.service.memory.bookid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IsbnJdkMemoryRepositoryTest {
    IsbnJdkMemoryRepository repository = new IsbnJdkMemoryRepository();

    @BeforeEach
    void beforeEach() {
        repository.init();
    }

    @Test
    void 조회결과가_0인_경우_null을_반환() {
        assertNull(repository.get("123"));
    }

    @Test
    void bookId를_0으로_세팅할_수_없음() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.add(new Isbn13MemoryData("123", 0)));
    }

}