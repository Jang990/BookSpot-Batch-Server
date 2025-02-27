package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.csv.book.YearParser;
import com.bookspot.batch.step.reader.file.csv.stock.StockCsvDataMapper;
import com.bookspot.batch.step.reader.file.csv.stock.StockCsvDelimiterTokenizer;
import com.bookspot.batch.step.writer.file.stock.StockCsvMetadataCreator;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

public class MultiStockCsvFileReader extends MultiResourceItemReader<StockCsvData> {

    public MultiStockCsvFileReader(YearParser yearParser) throws IOException {
        setName("multiStockCsvFileReader");
        setResources(
                new PathMatchingResourcePatternResolver()
                        .getResources(StockCsvMetadataCreator.MULTI_CSV_FILE_PATH)
        );

        setDelegate(
                new FlatFileItemReaderBuilder<StockCsvData>()
                        .name("stockCsvFileReader")
                        .encoding("euc-kr")
                        .lineTokenizer(new StockCsvDelimiterTokenizer())
                        .fieldSetMapper(new StockCsvDataMapper(yearParser))
                        .recordSeparatorPolicy(new DefaultRecordSeparatorPolicy())
                        .linesToSkip(1)
                        .build()
        );
    }
    
}
