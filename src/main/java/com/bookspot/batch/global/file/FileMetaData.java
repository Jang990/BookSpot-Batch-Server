package com.bookspot.batch.global.file;

import java.util.Objects;

public record FileMetaData(String name, String directory, FileFormat format) {
    public FileMetaData {
        Objects.requireNonNull(name);
        Objects.requireNonNull(directory);
        Objects.requireNonNull(format);
    }



    public String fullName() {
        return name.concat(format.getExt());
    }
}
