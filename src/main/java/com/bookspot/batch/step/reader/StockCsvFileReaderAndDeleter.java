package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.reader.file.csv.stock.StockCsvDataMapper;
import com.bookspot.batch.step.reader.file.csv.stock.StockCsvDelimiterTokenizer;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
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
    private final StepExecution stepExecution;
    private final Resource deleteTarget;

    public StockCsvFileReaderAndDeleter(StepExecution stepExecution, Resource resource) throws Exception {
        this.stepExecution = stepExecution;
        this.deleteTarget = resource;

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

    @Override
    public void close() throws ItemStreamException {
        super.close();

        if(!stepExecution.getExitStatus().equals(ExitStatus.COMPLETED))
            return;

        try {
            Files.delete(deleteTarget.getFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
