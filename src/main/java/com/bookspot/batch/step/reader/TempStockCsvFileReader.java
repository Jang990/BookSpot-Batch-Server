package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.reader.file.csv.stock.StockCsvDataMapper;
import com.bookspot.batch.step.reader.file.csv.stock.StockCsvDelimiterTokenizer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.core.io.Resource;

/**
 * #{stepExecutionContext['file']} Resource file 필요
 */
public class TempStockCsvFileReader extends FlatFileItemReader<StockCsvData> {
    public TempStockCsvFileReader(Resource resource) throws Exception {
        setName("stockCsvFileReader");
        setEncoding("euc-kr");
        setResource(resource);
        setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());
        setLinesToSkip(1);

        DefaultLineMapper<StockCsvData> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new StockCsvDelimiterTokenizer());
        lineMapper.setFieldSetMapper(new StockCsvDataMapper());
        setLineMapper(lineMapper);

        afterPropertiesSet();
    }
}
