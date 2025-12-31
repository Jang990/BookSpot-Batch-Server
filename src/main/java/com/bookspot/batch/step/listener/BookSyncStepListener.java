package com.bookspot.batch.step.listener;

import com.bookspot.batch.step.reader.IsbnReader;
import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;

@RequiredArgsConstructor
public class BookSyncStepListener implements StepExecutionListener {
    private final IsbnReader isbnReader;
    private final IsbnSet isbnSet;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        isbnReader.open(new ExecutionContext());

        try {
            isbnSet.init();
            while (true) {
                String isbn = isbnReader.read();
                if (isbn == null) {
                    isbnSet.beforeProcess();
                    return;
                }
                isbnSet.add(isbn);
            }
        } catch (Exception e) {
            throw new RuntimeException("bookSync.ISBN 캐시 로딩 실패", e);
        } finally {
            isbnReader.close();
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        isbnSet.clearAll();
        try {
            // CPU 사용량 32%까지 스파크.
            Thread.sleep(1_500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return StepExecutionListener.super.afterStep(stepExecution);
    }
}
