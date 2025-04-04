package com.bookspot.batch.global.properties.files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BookSpotFileProperties {
    @Value("${path.file.library}")
    private String library;

    @Value("${path.file.loan}")
    private String loan;

    public String library() {
        return library;
    }

    public String loan() {
        return loan;
    }
}
