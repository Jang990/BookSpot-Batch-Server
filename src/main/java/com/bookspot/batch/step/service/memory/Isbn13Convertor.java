package com.bookspot.batch.step.service.memory;

public class Isbn13Convertor {
    public static String convert(long isbn13) {
         return "%013d".formatted(isbn13);
    }
}
