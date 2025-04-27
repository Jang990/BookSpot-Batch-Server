package com.bookspot.batch.data;

import java.util.List;

public record LibraryIds(long bookId, List<String> libraryIds) {
}
