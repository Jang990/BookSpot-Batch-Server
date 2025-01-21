package com.bookspot.batch.global.file;

import java.util.Objects;

public record FileMetadata(String name, String directory, FileFormat format) {
    public FileMetadata {
        Objects.requireNonNull(name);
        Objects.requireNonNull(directory);
        Objects.requireNonNull(format);
    }

    public String fullName() {
        return name.concat(format.getExt());
    }

    public String absolutePath() {
        return directory.concat("/").concat(fullName());
    }
}
