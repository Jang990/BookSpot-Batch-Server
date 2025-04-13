package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.global.file.spec.NormalizedStockCsvSpec;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;

import java.util.Arrays;

public class StockNormalizedFileReader extends FlatFileItemReader<LibraryStock> {
    public StockNormalizedFileReader(String sourceFilePath) {
        setName("stockNormalizedFileReader");
        setEncoding("UTF-8");
        setResource(new FileSystemResource(sourceFilePath));

        DelimitedLineTokenizer tokenizer = delimitedLineTokenizer();
        DefaultLineMapper<LibraryStock> lineMapper = lineMapper(tokenizer);
        setLineMapper(lineMapper);
    }

    private DefaultLineMapper<LibraryStock> lineMapper(DelimitedLineTokenizer tokenizer) {
        DefaultLineMapper<LibraryStock> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSet ->
                new LibraryStock(
                        fieldSet.readLong(NormalizedStockCsvSpec.LIBRARY_ID.value()),
                        fieldSet.readLong(NormalizedStockCsvSpec.BOOK_ID.value())
                )
        );
        return lineMapper;
    }

    private DelimitedLineTokenizer delimitedLineTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(
                Arrays.stream(NormalizedStockCsvSpec.values())
                        .map(NormalizedStockCsvSpec::value)
                        .toArray(String[]::new)
        );
        return tokenizer;
    }
}
