package com.bookspot.batch.step;

import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.reader.IsbnIdReader;
import com.bookspot.batch.step.service.memory.bookid.Isbn13MemoryData;
import com.bookspot.batch.step.service.memory.loan.LoanCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class LoanMapStepConfig {
    private static final int CHUNK_SIZE = 3_000;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final StepLoggingListener stepLoggingListener;
    private final LoanCountService loanCountService;

    @Bean
    public Step loanMapInitStep(
            IsbnIdReader isbnIdReader) {
        return new StepBuilder("loanCountMapInitStep", jobRepository)
                .<Isbn13MemoryData, Isbn13MemoryData>chunk(CHUNK_SIZE, transactionManager)
                .reader(isbnIdReader)
                .writer(loanCountMapWriter())
                .listener(stepLoggingListener)
                .listener(
                        new StepExecutionListener() {
                            @Override
                            public void beforeStep(StepExecution stepExecution) {
                                loanCountService.init();
                                StepExecutionListener.super.beforeStep(stepExecution);
                            }

                            @Override
                            public ExitStatus afterStep(StepExecution stepExecution) {
                                loanCountService.beforeCount();
                                return StepExecutionListener.super.afterStep(stepExecution);
                            }
                        }
                )
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

    @Bean
    public ItemWriter<Isbn13MemoryData> loanCountMapWriter() {
        return chunk -> {
            for (Isbn13MemoryData data : chunk)
                loanCountService.add(data.isbn13());
        };
    }
}
