package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.global.file.spec.CleansingStockCsvSpec;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.Resource;

import java.util.Arrays;

public class CleansingStockFileReader extends FlatFileItemReader<LibraryStock> {
    public CleansingStockFileReader(Resource sourceFile) {
        setName("cleansingStockFileReader");
        setEncoding("UTF-8");
        setResource(sourceFile);

        DelimitedLineTokenizer tokenizer = delimitedLineTokenizer();
        DefaultLineMapper<LibraryStock> lineMapper = lineMapper(tokenizer);
        setLineMapper(lineMapper);
    }

    private DefaultLineMapper<LibraryStock> lineMapper(DelimitedLineTokenizer tokenizer) {
        DefaultLineMapper<LibraryStock> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSet ->
                new LibraryStock(
                        fieldSet.readLong(CleansingStockCsvSpec.LIBRARY_ID.value()),
                        fieldSet.readLong(CleansingStockCsvSpec.BOOK_ID.value())
                )
        );
        return lineMapper;
    }

    private DelimitedLineTokenizer delimitedLineTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(
                Arrays.stream(CleansingStockCsvSpec.values())
                        .map(CleansingStockCsvSpec::value)
                        .toArray(String[]::new)
        );
        return tokenizer;
    }
}
