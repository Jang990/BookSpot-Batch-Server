package com.bookspot.batch.global.file.stock;

import com.bookspot.batch.global.file.FileFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class StockFilenameUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String create(StockFilenameElement element) {
        return String.format("%d_%s", element.libraryId(), element.referenceDate());
    }

    public static String toNormalized(String filename) {
        StockFilenameElement element = parse(filename);
        return String.format("%d_%s_normalized", element.libraryId(), element.referenceDate());
    }

    public static String toFiltered(String filename) {
        StockFilenameElement element = parse(filename);
        return String.format("%d_%s_filtered", element.libraryId(), element.referenceDate());
    }

    public static StockFilenameElement parse(String filename) {
        if(hasExt(filename))
            filename = removeExt(filename);

        String[] args = filename.split("_");
        if(args.length < 2)
            throw new IllegalArgumentException();

        return new StockFilenameElement(
                Long.parseLong(args[0]),
                LocalDate.parse(args[1], formatter)
        );
    }

    private static boolean hasExt(String filename) {
        return Arrays.stream(FileFormat.values())
                .anyMatch(format -> filename.endsWith(format.getExt()));
    }

    private static String removeExt(String filename) {
        FileFormat fileFormat = Arrays.stream(FileFormat.values())
                .filter(format -> filename.endsWith(format.getExt()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        return filename.substring(0, filename.lastIndexOf(fileFormat.getExt()));
    }
}
