package com.bookspot.batch.data.document;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface DocumentIdentifiable {
    // null은 ES 자동 설정(UUID)에 맏긴다.
    @JsonIgnore
    default String getDocumentId() {
        return null;
    }
}
