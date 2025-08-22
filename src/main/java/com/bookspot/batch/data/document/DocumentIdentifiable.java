package com.bookspot.batch.data.document;

public interface DocumentIdentifiable {
    // null은 ES 자동 설정(UUID)에 맏긴다.
    default String getDocumentId() {
        return null;
    }
}
