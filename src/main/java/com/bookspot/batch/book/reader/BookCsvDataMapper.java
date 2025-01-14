package com.bookspot.batch.book.reader;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookCsvDataMapper implements FieldSetMapper<BookCsvData> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    @Override
    public BookCsvData mapFieldSet(FieldSet fieldSet) throws BindException {
        BookCsvData result = new BookCsvData();

        result.setControlNumber(fieldSet.readString("controlNumber"));
        result.setAuthorName(fieldSet.readString("authorName"));
        result.setVolumeName(fieldSet.readString("volumeName"));
        result.setPublicationYear(fieldSet.readString("publicationYear"));
        result.setClassificationNumber(fieldSet.readString("classificationNumber"));
        result.setBookSymbolNumber(fieldSet.readString("bookSymbolNumber"));
        result.setTitleName(fieldSet.readString("titleName"));
        result.setLibraryCode(fieldSet.readString("libraryCode"));
        result.setIsbn13Number(fieldSet.readString("isbn13Number"));
        result.setRepresentativeBook(fieldSet.readString("representativeBook"));
        result.setRegisterNumber(fieldSet.readString("registerNumber"));
        result.setIncomeFlagName(fieldSet.readString("incomeFlagName"));
        result.setManageFlagName(fieldSet.readString("manageFlagName"));
        result.setMediaFlagName(fieldSet.readString("mediaFlagName"));
        result.setUtilizationLimitFlagName(fieldSet.readString("utilizationLimitFlagName"));
        result.setUtilizationTargetFlagName(fieldSet.readString("utilizationTargetFlagName"));
        result.setAccompanyDataName(fieldSet.readString("accompanyDataName"));
        result.setSingleVolumeIsbn(fieldSet.readString("singleVolumeIsbn"));
        result.setSingleVolumeIsbnAdditionalSymbolName(fieldSet.readString("singleVolumeIsbnAdditionalSymbolName"));
        result.setClassificationSymbolFlagName(fieldSet.readString("classificationSymbolFlagName"));
        result.setVolumeSymbolName(fieldSet.readString("volumeSymbolName"));
        result.setDuplicateCopySymbolName(fieldSet.readString("duplicateCopySymbolName"));
        result.setRegisterDate(LocalDate.parse(fieldSet.readString("registerDate"), formatter));
        result.setIsbn13OriginalNumber(fieldSet.readString("isbn13OriginalNumber"));
        result.setMasterLibraryCode(fieldSet.readString("masterLibraryCode"));
        result.setVolumeExists(fieldSet.readString("volumeExists"));
        result.setSetIsbnChanged(fieldSet.readString("setIsbnChanged"));
        result.setVolumeOriginalName(fieldSet.readString("volumeOriginalName"));
        result.setTitleSubstituteName(fieldSet.readString("titleSubstituteName"));
        result.setKdcName(fieldSet.readString("kdcName"));
        result.setBookClassificationCode(fieldSet.readString("bookClassificationCode"));
        result.setBookLocationCode(fieldSet.readString("bookLocationCode"));

        return result;
    }

    private long toLong(String value) {
        return value.isBlank() ? 0 : Long.parseLong(value);
    }

    private int toInt(String value) {
        return value.isBlank() ? 0 : Integer.parseInt(value);
    }
}
