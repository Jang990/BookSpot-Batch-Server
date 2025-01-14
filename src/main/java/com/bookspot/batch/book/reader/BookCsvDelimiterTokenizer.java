package com.bookspot.batch.book.reader;

import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.batch.item.file.transform.DefaultFieldSetFactory;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;

import java.io.StringReader;
import java.util.Arrays;

public class BookCsvDelimiterTokenizer implements LineTokenizer {
    private static final char QUOTE = '"';
    private static final String[] fieldNames = {
            "controlNumber", "authorName", "volumeName",
            "publicationYear", "classificationNumber", "bookSymbolNumber",
            "titleName", "libraryCode", "isbn13Number",
            "representativeBook", "registerNumber", "incomeFlagName",
            "manageFlagName", "mediaFlagName", "utilizationLimitFlagName",
            "utilizationTargetFlagName", "accompanyDataName", "singleVolumeIsbn",
            "singleVolumeIsbnAdditionalSymbolName", "classificationSymbolFlagName", "volumeSymbolName",
            "duplicateCopySymbolName", "registerDate", "isbn13OriginalNumber",
            "masterLibraryCode", "volumeExists", "setIsbnChanged",
            "volumeOriginalName", "titleSubstituteName", "kdcName",
            "bookClassificationCode", "bookLocationCode"
    };

    @SneakyThrows
    @Override
    public FieldSet tokenize(String line) {
        if(line == null)
            return null;

        CSVParser parser = CSVFormat.DEFAULT
                .builder()
                .setQuote(QUOTE)
                .setIgnoreSurroundingSpaces(true)
                .build().parse(new StringReader(line));

        return new DefaultFieldSetFactory()
                .create(
                        Arrays.copyOf(
                                toFieldValues(parser),
                                fieldNames.length
                        ),
                        fieldNames
                );
    }

    private static String[] toFieldValues(CSVParser parser) {
        return parser.getRecords().getFirst()
                .stream().toList()
                .toArray(String[]::new);
    }
}
