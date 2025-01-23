package com.bookspot.batch.step.writer.file.stock;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StockFilenameUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String create(StockFilenameElement element) {
        return String.format("%d_%s", element.libraryId(), element.referenceDate());
    }

    public static StockFilenameElement parse(String filename) {
        String[] args = filename.split("_");
        if(args.length < 2)
            throw new IllegalArgumentException();

        return new StockFilenameElement(
                Long.parseLong(args[0]),
                LocalDate.parse(args[1], formatter)
        );
    }
}
