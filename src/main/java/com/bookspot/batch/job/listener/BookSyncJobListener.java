package com.bookspot.batch.job.listener;

import com.bookspot.batch.step.reader.IsbnReader;
import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.item.ExecutionContext;

@RequiredArgsConstructor
public class BookSyncJobListener implements JobExecutionListener {
    private final IsbnReader isbnReader;
    private final IsbnSet isbnSet;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        isbnReader.open(new ExecutionContext());

        try {
            while (true) {
                String isbn = isbnReader.read();
                if(isbn == null)
                    return;
                isbnSet.add(isbn);
            }
        } catch (Exception e) {
            throw new RuntimeException("bookSync.ISBN 캐시 로딩 실패", e);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        isbnSet.clearAll();
    }
}
