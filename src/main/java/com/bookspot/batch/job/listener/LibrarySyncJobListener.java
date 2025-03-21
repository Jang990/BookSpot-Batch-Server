package com.bookspot.batch.job.listener;

import com.bookspot.batch.step.reader.file.excel.library.LibraryFileDownloader;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@RequiredArgsConstructor
public class LibrarySyncJobListener implements JobExecutionListener {
    private final LibraryFileDownloader libraryFileDownloader;


    @Override
    public void beforeJob(JobExecution jobExecution) {
        libraryFileDownloader.download();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        libraryFileDownloader.delete();
    }
}
