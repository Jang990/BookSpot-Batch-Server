package com.bookspot.batch.stock.reader;

import com.bookspot.batch.book.processor.YearParser;
import com.bookspot.batch.step.writer.file.stock.StockCsvMetadataCreator;
import com.bookspot.batch.stock.data.LibraryStockCsvData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class StockReaderConfig {
    private final YearParser yearParser;

    @Bean
    @StepScope
    public FlatFileItemReader<LibraryStockCsvData> bookStockCsvFileReader() {
        return new FlatFileItemReaderBuilder<LibraryStockCsvData>()
                .name("bookStockCsvFileReader")
                .encoding("euc-kr")
                .lineTokenizer(new StockCsvDelimiterTokenizer())
                .fieldSetMapper(new StockCsvDataMapper(yearParser))

                /*
                196471	"천사의 시간 : 갖고 싶은 나만의 천사, 행운과 사랑을 전하는 종이접기
                    "	닉 로빈슨	별빛책방		9.79119E+12					1	0	2022-03-24
                 위와 같은 줄바꿈이 있는 csv 데이터를 파싱하기 위해서는 디폴트로 설정해주어야 함.
                 기본으로 들어가는 Simple Policy는 줄 단위로 읽어서 오류가 발생.
                 */
                .recordSeparatorPolicy(new DefaultRecordSeparatorPolicy())
                .linesToSkip(1)
                .build();
    }

    @Bean
    @StepScope
    public MultiResourceItemReader<LibraryStockCsvData> multiBookStockCsvFileReader() {
        MultiResourceItemReader<LibraryStockCsvData> reader = new MultiResourceItemReader<>();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {

            Resource[] resources = resolver.getResources(getFilePath());
            reader.setResources(resources);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load files", e);
        }

        reader.setDelegate(bookStockCsvFileReader());
        return reader;
    }

    private static String getFilePath() {
        // file:bookSpotFiles/stock/*.csv
        return "file:".concat(
                StockCsvMetadataCreator.create("*").fullName());
    }
}
