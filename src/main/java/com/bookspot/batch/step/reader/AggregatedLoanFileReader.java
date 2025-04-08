package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.file.csv.AggregatedBook;
import com.bookspot.batch.global.file.spec.AggregatedBooksCsvSpec;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;

import java.util.Arrays;

public class AggregatedLoanFileReader extends FlatFileItemReader<AggregatedBook> {
    public AggregatedLoanFileReader(String sourceFilePath) {
        setName("aggregatedBookCsvFileReader");
        setEncoding("UTF-8");
        setResource(new FileSystemResource(sourceFilePath));

        DelimitedLineTokenizer tokenizer = delimitedLineTokenizer();

        DefaultLineMapper<AggregatedBook> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSet ->
                new AggregatedBook(
                        fieldSet.readString(AggregatedBooksCsvSpec.ISBN13.value()),
                        fieldSet.readInt(AggregatedBooksCsvSpec.LOAN_COUNT.value())
                )
        );

        setLineMapper(lineMapper);
    }

    private DelimitedLineTokenizer delimitedLineTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(
                Arrays.stream(AggregatedBooksCsvSpec.values())
                        .map(AggregatedBooksCsvSpec::value)
                        .toArray(String[]::new)
        );
        return tokenizer;
    }
}
