package com.bookspot.batch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TempServiceTest {
    @Autowired
    TempService service;

    @Test
    void test() {
        service.createIndex();
    }
}