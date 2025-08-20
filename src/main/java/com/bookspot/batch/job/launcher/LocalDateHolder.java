package com.bookspot.batch.job.launcher;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class LocalDateHolder {
    LocalDate now() {
        return LocalDate.now();
    }
}
