package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.reader.file.csv.stock.StockCsvDataMapper;
import com.bookspot.batch.step.reader.file.csv.stock.StockCsvDelimiterTokenizer;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;

/**
 * 주의) 읽고 난 이후에 파일 제거
 * #{stepExecutionContext['file']} Resource file 필요
 */
public class StockCsvFileReaderAndDeleter extends FlatFileItemReader<StockCsvData> {
    private final Resource deleteTarget;
    public StockCsvFileReaderAndDeleter(Resource resource) throws Exception {
        setName("stockCsvFileReader");
        setEncoding("euc-kr");
        setResource(resource);
        deleteTarget = resource;

        setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());
        setLinesToSkip(1);

        DefaultLineMapper<StockCsvData> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new StockCsvDelimiterTokenizer());
        lineMapper.setFieldSetMapper(new StockCsvDataMapper());
        setLineMapper(lineMapper);

        afterPropertiesSet();
    }

    @Override
    public void close() throws ItemStreamException {
        super.close();

        try {
            Files.delete(deleteTarget.getFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
