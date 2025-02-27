package com.bookspot.batch.step.reader.config;

import com.bookspot.batch.data.file.csv.StockCsvData;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

class StockFileReaderConfigTest {
    StockFileReaderConfig config = new StockFileReaderConfig();
    Map<String, StockCsvData> repository = new HashMap<>();

    @Test
    void test() throws Exception {
        HashMap<String, Integer> map12 = new HashMap<>();
        HashMap<String, Integer> map01 = new HashMap<>();
        FlatFileItemReader<StockCsvData> file12 = config.stockCsvFileReader("src/main/resources/mytest/1_2024-12-01.csv");
        FlatFileItemReader<StockCsvData> file01 = config.stockCsvFileReader("src/main/resources/mytest/1_2025-01-01.csv");
        file12.open(new ExecutionContext());
        file01.open(new ExecutionContext());

        /*
        12월 데이터 수 : 31524
        12월 중복 제외 데이터 수 : 28463
        12월 중복 데이터 수 : 1595
        12월 중복된 권 수 : 3061

        1월 데이터 수 : 30803 (-721)
        1월 중복 제외 데이터 수 : 27823 (-640)
        1월 중복 데이터 수 : 1577 (-18)
        1월 중복된 권 수 : 2980 (-81)

        추가된 책 10개.
        사라진 책 650개.
        => 중복 제외 데이터 수 -640(= +10-650)개와 일치한다.
        */
        print(12, file12, map12);
        System.out.println();
        print(1, file01, map01);

        printDiffData(map12, map01);
    }

    private void print(int month, FlatFileItemReader<StockCsvData> file12, HashMap<String, Integer> map12) throws Exception {
        int count12 = readAll(file12, map12);
        System.out.println(month + "월 데이터 수 : " + count12);
        System.out.println(month + "월 중복 제외 데이터 수 : " + map12.size());

        int dupCount = (int) map12.keySet().stream().filter(key -> map12.get(key) > 1).count();
        System.out.println(month + "월 여러번 등장한 책 종류 수 : " + dupCount);

        int dupSum = map12.keySet().stream().filter(key -> map12.get(key) > 1).mapToInt(map12::get).sum() - dupCount;
        System.out.println(month + "월 여러번 등장한 책 개수 : " + dupSum);
    }

    private void printDiffData(Map<String, Integer> map1, Map<String, Integer> map2) throws Exception {
        HashSet<String> duplicated = new HashSet<>(map1.keySet());
        duplicated.retainAll(map2.keySet());

        HashSet<String> unique = new HashSet<>(map1.keySet());
        unique.removeAll(duplicated);

        System.out.println();
        System.out.println(unique.size());
    }

    private int readAll(FlatFileItemReader<StockCsvData> reader, Map<String, Integer> map) throws Exception {
        int cnt = 0;
        StockCsvData data;
        while ((data = reader.read()) != null) {
            String isbn = data.getIsbn();
            map.put(isbn, map.getOrDefault(isbn, 0) + 1);
            cnt++;
        }
        return cnt;
    }

}