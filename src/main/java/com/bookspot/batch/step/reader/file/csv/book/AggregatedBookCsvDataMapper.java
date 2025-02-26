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

    @Override
    public AggregatedBook mapFieldSet(FieldSet fieldSet) throws BindException {
        return new AggregatedBook(
                read(fieldSet, AggregatedBooksCsvSpec.ISBN13),
                readInt(fieldSet, AggregatedBooksCsvSpec.LOAN_COUNT)
        );
    }

    private int readInt(FieldSet fieldSet, AggregatedBooksCsvSpec spec) {
        return fieldSet.readInt(spec.value());
    }

    private String read(FieldSet fieldSet, AggregatedBooksCsvSpec spec) {
        return fieldSet.readString(spec.value());
    }
}
