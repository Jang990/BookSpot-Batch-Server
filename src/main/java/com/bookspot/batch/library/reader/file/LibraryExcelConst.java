package com.bookspot.batch.library.reader.file;

import com.bookspot.batch.BookSpotFileConst;
import com.bookspot.batch.global.file.FileFormat;
import com.bookspot.batch.global.file.FileMetadata;

class LibraryExcelConst {
    private static final String DIRECTORY_NAME = BookSpotFileConst.ROOT_DIRECTORY.concat("/library");
    private static final String FILE_NAME = "library_list";

    protected static final FileMetadata metadata = new FileMetadata(FILE_NAME, DIRECTORY_NAME, FileFormat.EXCEL);
}
