package com.bookspot.batch.step.reader.file.csv.stock;

import com.bookspot.batch.global.file.spec.LibraryStockCsvSpec;
import com.bookspot.batch.data.file.csv.StockCsvData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class StockCsvDataMapper implements FieldSetMapper<StockCsvData> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public StockCsvData mapFieldSet(FieldSet fieldSet) throws BindException {
        return new StockCsvData(
                read(fieldSet, LibraryStockCsvSpec.TITLE),
                read(fieldSet, LibraryStockCsvSpec.AUTHOR),
                read(fieldSet, LibraryStockCsvSpec.PUBLISHER),
                read(fieldSet, LibraryStockCsvSpec.PUBLICATION_YEAR),
                read(fieldSet, LibraryStockCsvSpec.ISBN),
                read(fieldSet, LibraryStockCsvSpec.VOLUME),
                read(fieldSet, LibraryStockCsvSpec.SUBJECT_CODE),
                toInt(read(fieldSet, LibraryStockCsvSpec.NUMBER_OF_BOOKS)),
                toInt(read(fieldSet, LibraryStockCsvSpec.LOAN_COUNT))
        );
    }

    private String read(FieldSet fieldSet, LibraryStockCsvSpec spec) {
        return fieldSet.readString(spec.value());
    }

    private int toInt(String value) {
        return value == null || value.isBlank() ? 0 : Integer.parseInt(value);
    }
}
