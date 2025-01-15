package com.bookspot.batch.book.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.util.regex.Matcher;

@RequiredArgsConstructor
public class BookCsvDataMapper implements FieldSetMapper<BookCsvData> {
    private final YearParser yearParser;

    @Override
    public BookCsvData mapFieldSet(FieldSet fieldSet) throws BindException {
        BookCsvData result = new BookCsvData();
        result.setIsbn13(read(fieldSet, BookCsvSpec.ISBN13_NUMBER));
        result.setAuthor(read(fieldSet, BookCsvSpec.AUTHOR_NAME));
        result.setPublicationYear(yearParser.parse(read(fieldSet, BookCsvSpec.PUBLICATION_YEAR)));
        result.setTitle(read(fieldSet, BookCsvSpec.TITLE_NAME));
        result.setSubjectCode(read(fieldSet, BookCsvSpec.CLASSIFICATION_NUMBER));
        result.setVolume(read(fieldSet, BookCsvSpec.VOLUME_NAME));

        return result;
    }
    private String read(FieldSet fieldSet, BookCsvSpec spec) {
        String result = fieldSet.readString(spec.value());
        if(result == null || result.isBlank())
            return null;
        return result;
    }
}
