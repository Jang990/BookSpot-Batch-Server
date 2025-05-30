package com.bookspot.batch.job.loan;

import com.bookspot.batch.job.validator.file.CustomFilePathValidators;
import com.bookspot.batch.job.validator.file.FilePathType;
import com.bookspot.batch.job.validator.FilePathJobParameterValidator;
import com.bookspot.batch.step.writer.file.book.AggregatedBooksCsvWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
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

    public static final String OUTPUT_FILE_PARAM_NAME = "outputFilePath";
    public static final String OUTPUT_FILE_PATH = "#{jobParameters['outputFilePath']}";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final AggregatedBooksCsvWriter aggregatedBooksCsvWriter;

    private final CustomFilePathValidators filePathValidators;

    @Bean
    public Job loanAggregatedJob(Flow loanAggregatedFlow) {
        return new JobBuilder("loanAggregatedJob", jobRepository)
                .validator(
                        FilePathJobParameterValidator.of(
                                filePathValidators,
                                Map.of(
                                        DIRECTORY_PARAM_NAME, FilePathType.REQUIRED_DIRECTORY,
                                        OUTPUT_FILE_PARAM_NAME, FilePathType.OPTIONAL_FILE
                                )
                        )
                )
                .start(loanAggregatedFlow)
                .end()
                .build();
    }

    @Bean
    public Flow loanAggregatedFlow(
            AggregationCompletedDecider aggregationCompletedDecider,
            Step loanMapInitStep,
            Step readLoanCountMasterStep,
            Step loanMapCleaningStep,
            Step syncLoanCountStep) {
        return new FlowBuilder<Flow>("loanAggregatedFlow")
                .start(aggregationCompletedDecider)
                    .on(AggregationCompletedDecider.SKIP_AGGREGATION)
                        .to(syncLoanCountStep)
                .from(aggregationCompletedDecider)
                    .on(AggregationCompletedDecider.EXECUTE_ALL)
                        .to(loanMapInitStep)
                        .next(readLoanCountMasterStep) // 도서관 재고 파일(1500개) 정보의 ISBN과 LOAN_COUNT를 메모리에 저장
                        .next(aggregateBookFileStep())// 인메모리에 저장한 정보를 파일로 저장
                        .next(loanMapCleaningStep)
                        .next(syncLoanCountStep)
                .build();
    }

    @Bean
    public Step aggregateBookFileStep() {
        return new StepBuilder(AggregationCompletedDecider.CREATION_FILE_STEP_NAME, jobRepository)
                .tasklet(saveAggregatedCsvTasklet(null), transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet saveAggregatedCsvTasklet(
            @Value(OUTPUT_FILE_PATH) String aggregatedFilePath) {
        return (contribution, chunkContext) -> {
            aggregatedBooksCsvWriter.saveToCsv(aggregatedFilePath);
            return RepeatStatus.FINISHED;
        };
    }
}
