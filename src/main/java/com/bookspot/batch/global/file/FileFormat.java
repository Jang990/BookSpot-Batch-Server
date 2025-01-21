package com.bookspot.batch.global.file;

public enum FileFormat {
    CSV(".csv"),
    EXCEL(".xlsx");

    private final String ext;

    FileFormat(String ext) {
        this.ext = ext;
    }

    public String getExt() {
        return ext;
    }
}
