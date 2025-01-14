package com.bookspot.batch.book.reader;

import com.bookspot.batch.book.data.Book;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class BookCsvDataMapper implements FieldSetMapper<Book> {

    @Override
    public Book mapFieldSet(FieldSet fieldSet) throws BindException {
        Book result = new Book();
        result.setIsbn13(read(fieldSet, BookCsvSpec.ISBN13_NUMBER));
        result.setAuthor(read(fieldSet, BookCsvSpec.AUTHOR_NAME));
        result.setPublicationYear(toInt(read(fieldSet, BookCsvSpec.PUBLICATION_YEAR)));
        result.setTitle(readTitle(fieldSet));
        result.setSubjectCode(read(fieldSet, BookCsvSpec.CLASSIFICATION_NUMBER));
        return result;
    }

    private int toInt(String value) {
        return Integer.parseInt(value);
    }

    private String readTitle(FieldSet fieldSet) {
        String volumeExists = read(fieldSet, BookCsvSpec.VOLUME_EXISTS);
        if(volumeExists == null || !(volumeExists.equals("Y") || volumeExists.equals("N")))
            throw new IllegalArgumentException("representativeBook 필드 오류 - %s".formatted(volumeExists));

        if(volumeExists.equals("N"))
            return read(fieldSet, BookCsvSpec.TITLE_NAME);

        StringBuilder sb = new StringBuilder();
        sb.append(read(fieldSet, BookCsvSpec.TITLE_NAME)).append(" ")
                .append(read(fieldSet, BookCsvSpec.VOLUME_NAME)).append("권");
        return sb.toString();
    }

    private String read(FieldSet fieldSet, BookCsvSpec spec) {
        String result = fieldSet.readString(spec.value());
        if(result == null || result.isBlank())
            return null;
        return result;
    }
}
