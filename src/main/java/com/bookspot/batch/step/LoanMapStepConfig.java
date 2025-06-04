package com.bookspot.batch.step;

import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.reader.IsbnIdReader;
import com.bookspot.batch.step.service.memory.bookid.Isbn13MemoryData;
import com.bookspot.batch.step.service.memory.loan.LoanCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class LoanMapStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final StepLoggingListener stepLoggingListener;
    private final LoanCountService loanCountService;

    @Bean
    public Step loanMapInitStep(
            IsbnIdReader isbnIdReader) {
        return new StepBuilder("loanCountMapInitStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    loanCountService.init();
                    isbnIdReader.open(new ExecutionContext());

                    Isbn13MemoryData data;
                    while ((data = isbnIdReader.read()) != null)
                        loanCountService.add(data.isbn13());

                    isbnIdReader.close();
                    loanCountService.beforeCount();
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .listener(stepLoggingListener)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step loanMapCleaningStep() {
        return new StepBuilder("loanMapCleaningStep", jobRepository)
                .tasklet(
                        (contribution, chunkContext) -> {
                            loanCountService.clearAll();
                            return RepeatStatus.FINISHED;
                        },
                        transactionManager
                )
                .allowStartIfComplete(true)
                .build();
    }
}
