package com.bookspot.batch.step.listener;

import com.bookspot.batch.step.reader.IsbnIdReader;
import com.bookspot.batch.step.service.memory.bookid.Isbn13MemoryData;
import com.bookspot.batch.step.service.memory.loan.LoanCountService;
import com.bookspot.batch.step.service.AggregatedBooksCsvWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;

import java.io.IOException;

@RequiredArgsConstructor
public class LoanCountStepListener implements StepExecutionListener {
    private final LoanCountService loanCountService;
    private final IsbnIdReader isbnIdReader;
    private final AggregatedBooksCsvWriter aggregatedBooksCsvWriter;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        StepExecutionListener.super.beforeStep(stepExecution);
        initLoanCountService();
    }

    private void initLoanCountService() {
        try {
            loanCountService.init();
            isbnIdReader.open(new ExecutionContext());

            Isbn13MemoryData data;
            while ((data = isbnIdReader.read()) != null)
                loanCountService.add(data.isbn13());

            loanCountService.beforeCount();
        } catch (Exception e) {
            loanCountService.clearAll();
            throw new RuntimeException(e);
        } finally {
            isbnIdReader.close();
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if(stepExecution.getExitStatus().equals(ExitStatus.COMPLETED))
            saveLoanCountFile();
        return StepExecutionListener.super.afterStep(stepExecution);
    }

    private void saveLoanCountFile() {
        try {
            aggregatedBooksCsvWriter.saveToCsv();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            loanCountService.clearAll();
        }
    }
}
