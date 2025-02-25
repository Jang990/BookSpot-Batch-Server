package com.bookspot.batch.step.reader.file.csv.book;

import com.bookspot.batch.data.file.csv.AggregatedBook;
import com.bookspot.batch.global.file.spec.AggregatedBooksCsvSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class AggregatedBookCsvDataMapper implements FieldSetMapper<AggregatedBook> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public AggregatedBook mapFieldSet(FieldSet fieldSet) throws BindException {
        return new AggregatedBook(
                read(fieldSet, AggregatedBooksCsvSpec.ISBN13),
                toInt(read(fieldSet, AggregatedBooksCsvSpec.SUBJECT_CODE)),
                toInt(read(fieldSet, AggregatedBooksCsvSpec.LOAN_COUNT))
        );
    }

    private String read(FieldSet fieldSet, AggregatedBooksCsvSpec spec) {
        return fieldSet.readString(spec.value());
    }

    private int toInt(String value) {
        return value.isBlank() ? 0 : Integer.parseInt(value);
    }
}
