package com.bookspot.batch.global.properties.files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BookSpotDirectoryProperties {
    @Value("${path.directory.root}")
    private String root;
    @Value("${path.directory.librarySync}")
    private String librarySync;
    @Value("${path.directory.bookSync}")
    private String bookSync;
    @Value("${path.directory.stockSync}")
    private String stockSync;
    @Value("${path.directory.loanSync}")
    private String loanSync;
    @Value("${path.directory.normalizedStock}")
    private String normalizedStock;


    public String root() {
        return root;
    }

    public String librarySync() {
        return librarySync;
    }

    public String bookSync() {
        return bookSync;
    }

    public String stockSync() {
        return stockSync;
    }

    public String loanSync() {
        return loanSync;
    }

    public String normalizedStock() {
        return normalizedStock;
    }
}