package com.bookspot.batch.job.listener;

import com.bookspot.batch.step.reader.IsbnIdReader;
import com.bookspot.batch.step.service.memory.bookid.Isbn13MemoryData;
import com.bookspot.batch.step.service.memory.loan.InMemoryLoanCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.item.ExecutionContext;

@RequiredArgsConstructor
public class LoanAggregatedJobListener implements JobExecutionListener {
    private final IsbnIdReader isbnIdReaderForWarmup;
    private final InMemoryLoanCountService loanCountService;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        try {
            isbnIdReaderForWarmup.open(new ExecutionContext());
            while (true) {
                Isbn13MemoryData data = isbnIdReaderForWarmup.read();
                if(data == null)
                    return;
                loanCountService.add(data.isbn13());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            isbnIdReaderForWarmup.close();
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        loanCountService.clearAll();
    }
}
