package com.bookspot.batch.step.processor;

import com.bookspot.batch.data.LibraryForFileParsing;
import com.bookspot.batch.data.crawler.StockFileData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
class StockFilePathParserBootTest {
    @Autowired
    StockFilePathParser stockFilePathParser;

//    @Test
    void test() {
        StockFileData result = stockFilePathParser.process(new LibraryForFileParsing(1L, "127058", "29981", null));
        System.out.println(result);
    }
}