package com.bookspot.batch.job.listener;

import com.bookspot.batch.step.reader.IsbnIdReader;
import com.bookspot.batch.step.service.memory.bookid.Isbn13MemoryData;
import com.bookspot.batch.step.service.memory.bookid.IsbnMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.item.ExecutionContext;

@RequiredArgsConstructor
public class StockSyncJobListener implements JobExecutionListener {
    public static final int ISBN_ID_WARMUP_SIZE = 10_000;

    private final IsbnIdReader isbnIdReader;
    private final IsbnMemoryRepository isbnEclipseMemoryRepository;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        isbnIdReader.open(new ExecutionContext());

        try {
            while (true) {
                Isbn13MemoryData isbn = isbnIdReader.read();
                if(isbn == null)
                    return;
                isbnEclipseMemoryRepository.add(isbn);
            }
        } catch (Exception e) {
            throw new RuntimeException("bookSync.ISBN 캐시 로딩 실패", e);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        isbnEclipseMemoryRepository.clearMemory();
    }
}
