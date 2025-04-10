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
    private final IsbnIdReader isbnIdReader;
    private final InMemoryLoanCountService loanCountService;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        isbnIdReader.open(new ExecutionContext());

        try {
            while (true) {
                Isbn13MemoryData data = isbnIdReader.read();
                if(data == null)
                    return;
                loanCountService.add(data.isbn13());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        loanCountService.clearAll();
    }
}
