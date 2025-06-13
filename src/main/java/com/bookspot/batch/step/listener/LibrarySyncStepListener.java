package com.bookspot.batch.step.listener;

import com.bookspot.batch.step.reader.file.excel.library.LibraryFileDownloader;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;

@RequiredArgsConstructor
public class LibrarySyncStepListener implements StepExecutionListener {
    private final LibraryFileDownloader libraryFileDownloader;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        libraryFileDownloader.download();
    }
}
