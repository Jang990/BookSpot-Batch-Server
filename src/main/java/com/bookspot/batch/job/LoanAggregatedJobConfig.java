package com.bookspot.batch.job;

import com.bookspot.batch.job.validator.file.CustomFilePathValidators;
import com.bookspot.batch.job.validator.file.FilePathType;
import com.bookspot.batch.job.validator.temp_FilePathJobParameterValidator;
import com.bookspot.batch.step.service.memory.loan.InMemoryLoanCountService;
import com.bookspot.batch.step.writer.file.book.AggregatedBooksCsvWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class LoanAggregatedJobConfig {
    public static final String DIRECTORY_PARAM_NAME = "outputDirectory";
    public static final String DIRECTORY_PATH = "#{jobParameters['outputDirectory']}";

    public static final String AGGREGATED_FILE_PARAM_NAME = "aggregatedFilePath";
    public static final String AGGREGATED_FILE_PATH = "#{jobParameters['aggregatedFilePath']}";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final InMemoryLoanCountService inMemoryBookService;
    private final AggregatedBooksCsvWriter aggregatedBooksCsvWriter;

    private final CustomFilePathValidators filePathValidators;

    @Bean
    public Job loanSyncJob(Step loadLoanCountToMemoryStep, Step syncAggregatedBookStep) {
        return new JobBuilder("bookLoanCountSyncJob", jobRepository)
                .start(loadLoanCountToMemoryStep) // 도서관 재고 파일(1500개) 정보의 ISBN과 LOAN_COUNT를 메모리에 저장
                .next(aggregateBookFileStep())// 인메모리에 저장한 정보를 파일로 저장
                .next(clearBookMemoryStep()) // 도서 정보 인메모리 clearAll();
                .next(syncAggregatedBookStep) //- 저장한 파일을 DB에 반영 - 새로 나온 책 + 최근 대출 횟수 반영
                .validator(
                        temp_FilePathJobParameterValidator.of(
                                filePathValidators,
                                Map.of(
                                        DIRECTORY_PARAM_NAME, FilePathType.REQUIRED_DIRECTORY,
                                        AGGREGATED_FILE_PARAM_NAME, FilePathType.OPTIONAL_FILE
                                )
                        )
                )
                .build();
    }

    @Bean
    public Step clearBookMemoryStep() {
        return new StepBuilder("clearBookMemoryStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    inMemoryBookService.clearAll();
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step aggregateBookFileStep() {
        return new StepBuilder("aggregateBookFileStep", jobRepository)
                .tasklet(saveAggregatedCsvTasklet(null), transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet saveAggregatedCsvTasklet(
            @Value(AGGREGATED_FILE_PATH) String aggregatedFilePath) {
        return (contribution, chunkContext) -> {
            aggregatedBooksCsvWriter.saveToCsv(aggregatedFilePath, inMemoryBookService.getData());
            return RepeatStatus.FINISHED;
        };
    }
}
