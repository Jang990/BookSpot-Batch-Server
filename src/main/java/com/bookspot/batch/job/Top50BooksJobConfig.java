package com.bookspot.batch.job;

import com.bookspot.batch.data.Top50Book;
import com.bookspot.batch.data.document.RankingAge;
import com.bookspot.batch.data.document.RankingGender;
import com.bookspot.batch.data.document.RankingType;
import com.bookspot.batch.infra.opensearch.BookRankingIndexSpec;
import com.bookspot.batch.infra.opensearch.IndexSpecCreator;
import com.bookspot.batch.infra.opensearch.OpenSearchRepository;
import com.bookspot.batch.step.book.api.Top50BookApiReader;
import com.bookspot.batch.step.book.api.Top50BookPartitioner;
import com.bookspot.batch.step.book.api.Top50BookWriter;
import com.bookspot.batch.step.reader.api.top50.RankingConditions;
import com.bookspot.batch.step.reader.api.top50.WeeklyTop50ApiRequester;
import com.bookspot.batch.step.service.BookCodeResolver;
import com.bookspot.batch.step.service.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class Top50BooksJobConfig {
    private static final int TOP_50 = 50;

    public static final String DAILY_SYNC_FLAG_PARAM_NAME = "dailySyncFlag";
    public static final String DAILY_SYNC_FLAG_PARAM = "#{jobParameters['dailySyncFlag']}";
    public static final String DAILY_SYNC_FLAG_PARAM_VALUE = "DAILY_SYNC_FLAG";

    public static final String REFERENCE_DATE_PARAM_NAME = "referenceDate";
    public static final String REFERENCE_DATE_PARAM = "#{jobParameters['referenceDate']}";

    public static final String A_REFERENCE_DATE_PARAM = "#{stepExecutionContext['referenceDate']}";

    public static final String COND_PERIOD_PARAM_NAME = "condPeriod";
    public static final String COND_PERIOD_PARAM = "#{stepExecutionContext['condPeriod']}";

    public static final String COND_AGE_PARAM_NAME = "condAge";
    public static final String COND_AGE_PARAM = "#{stepExecutionContext['condAge']}";

    public static final String COND_GENDER_PARAM_NAME = "condGender";
    public static final String COND_GENDER_PARAM = "#{stepExecutionContext['condGender']}";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final WeeklyTop50ApiRequester weeklyTop50ApiRequester;

    private final BookRepository bookRepository;
    private final OpenSearchRepository openSearchRepository;
    private final BookCodeResolver bookCodeResolver;

    private final IndexSpecCreator indexSpecCreator;

    @Bean
    @StepScope
    public Top50BookApiReader top50BookApiReader(
            @Value(A_REFERENCE_DATE_PARAM) LocalDate referenceDate,
            @Value(COND_PERIOD_PARAM) String periodType,
            @Value(COND_GENDER_PARAM) String gender,
            @Value(COND_AGE_PARAM) String age
    ) {
        return new Top50BookApiReader(
                referenceDate,
                new RankingConditions(
                        RankingType.valueOf(periodType),
                        RankingGender.valueOf(gender),
                        RankingAge.valueOf(age)
                ),
                weeklyTop50ApiRequester
        );
    }

    @Bean
    @StepScope
    public Top50BookWriter top50BookWriter(
            @Value(REFERENCE_DATE_PARAM) LocalDate referenceDate,
            @Value(DAILY_SYNC_FLAG_PARAM) String dailySyncFlag,
            @Value(COND_PERIOD_PARAM) String periodType,
            @Value(COND_GENDER_PARAM) String gender,
            @Value(COND_AGE_PARAM) String age
    ) {
        return new Top50BookWriter(
                referenceDate,
                getBookRankingIndexName(referenceDate, dailySyncFlag),
                new RankingConditions(
                        RankingType.valueOf(periodType),
                        RankingGender.valueOf(gender),
                        RankingAge.valueOf(age)
                ),
                bookRepository,
                openSearchRepository,
                bookCodeResolver
        );
    }

    private String getBookRankingIndexName(LocalDate referenceDate, String dailySyncFlag) {
        BookRankingIndexSpec bookRankingIndexSpec = indexSpecCreator.createRankingIndexSpec();
        if (DAILY_SYNC_FLAG_PARAM_VALUE.equals(dailySyncFlag))
            return bookRankingIndexSpec.dailyIndexName(referenceDate);
        else
            return bookRankingIndexSpec.serviceIndexName();
    }

    @Bean
    public Step bookTop50SyncStep() {
        return new StepBuilder("bookTop50SyncStep", jobRepository)
                .<Top50Book, Top50Book>chunk(TOP_50, transactionManager)
                .reader(top50BookApiReader(null, null, null, null))
                .writer(top50BookWriter(null, null, null, null, null))
                .build();
    }

    // TODO: 이전 주(4주전 작업한 내용) 삭제작업
    @Bean
    public Job weeklyTop50BooksJob(Step weeklyBookTop50SyncPartitionMasterStep) {
        return new JobBuilder("weeklyTop50BooksJob", jobRepository)
                .start(weeklyBookTop50SyncPartitionMasterStep)
                .build();
    }

    // TODO: 2달전 내용 삭제 작업
    @Bean
    public Job monthlyTop50BooksJob(Step monthlyBookTop50SyncPartitionMasterStep) {
        return new JobBuilder("monthlyTop50BooksJob", jobRepository)
                .start(monthlyBookTop50SyncPartitionMasterStep)
                .build();
    }

    @Bean
    public Job dailySyncTop50BooksJob(
            Step createBookDailyRankingIndexStep,
            Step weeklyBookTop50SyncPartitionMasterStep,
            Step monthlyBookTop50SyncPartitionMasterStep,
            Step deletePrevBookDailyRankingIndexStep
    ) {
        return new JobBuilder("dailySyncTop50BooksJob", jobRepository)
                .start(createBookDailyRankingIndexStep)
                .next(weeklyBookTop50SyncPartitionMasterStep)
                .next(monthlyBookTop50SyncPartitionMasterStep)
                .next(deletePrevBookDailyRankingIndexStep)
                .build();
    }


    @Bean
    public Step weeklyBookTop50SyncPartitionMasterStep(
            Step bookTop50SyncStep,
            TaskExecutorPartitionHandler bookTop50SyncPartitionHandler
    ) throws IOException {
        return new StepBuilder("weeklyBookTop50SyncPartitionMasterStep", jobRepository)
                .partitioner(
                        bookTop50SyncStep.getName(),
                        weeklyTop50BookPartitioner(null)
                )
                .partitionHandler(bookTop50SyncPartitionHandler)
                .build();
    }

    @Bean
    public Step monthlyBookTop50SyncPartitionMasterStep(
            Step bookTop50SyncStep,
            TaskExecutorPartitionHandler bookTop50SyncPartitionHandler
    ) throws IOException {
        return new StepBuilder("monthlyBookTop50SyncPartitionMasterStep", jobRepository)
                .partitioner(
                        bookTop50SyncStep.getName(),
                        monthlyTop50BookPartitioner(null)
                )
                .partitionHandler(bookTop50SyncPartitionHandler)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler bookTop50SyncPartitionHandler(
            Step bookTop50SyncStep,
            TaskExecutor singleApiTaskPool
    ) {
        // TODO: jpaRepository.save() 호출 시 데드락 가능성. 멀티스레드로 전환하려면 해결할 것
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(bookTop50SyncStep);
        partitionHandler.setTaskExecutor(singleApiTaskPool);
        partitionHandler.setGridSize(1);
        return partitionHandler;
    }

    @Bean
    @StepScope
    public Top50BookPartitioner weeklyTop50BookPartitioner(
            @Value(REFERENCE_DATE_PARAM) LocalDate referenceDate
    ) {
        return new Top50BookPartitioner(referenceDate, RankingType.WEEKLY);
    }

    @Bean
    @StepScope
    public Top50BookPartitioner monthlyTop50BookPartitioner(
            @Value(REFERENCE_DATE_PARAM) LocalDate referenceDate
    ) {
        return new Top50BookPartitioner(referenceDate, RankingType.MONTHLY);
    }


    // =============== 인덱스 관리 Step, Tasklet
    @Bean
    public Step createBookDailyRankingIndexStep() {
        return new StepBuilder("createBookDailyRankingIndexStep", jobRepository)
                .tasklet(createBookDailyRankingIndexTasklet(null), transactionManager)
                .build();
    }

    @Bean
    public Step deletePrevBookDailyRankingIndexStep() {
        return new StepBuilder("deleteBookDailyRankingIndexStep", jobRepository)
                .tasklet(deletePrevBookDailyRankingIndexTasklet(null), transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet createBookDailyRankingIndexTasklet(
            @Value(REFERENCE_DATE_PARAM) LocalDate referenceDate
    ) {
        BookRankingIndexSpec bookRankingIndexSpec = indexSpecCreator.createRankingIndexSpec();
        return (contribution, chunkContext) -> {
            openSearchRepository.createIndex(
                    bookRankingIndexSpec.dailyIndexName(referenceDate),
                    BookRankingIndexSpec.SCHEMA
            );
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @StepScope
    public Tasklet deletePrevBookDailyRankingIndexTasklet(
            @Value(REFERENCE_DATE_PARAM) LocalDate referenceDate
    ) {
        BookRankingIndexSpec bookRankingIndexSpec = indexSpecCreator.createRankingIndexSpec();
        return (contribution, chunkContext) -> {
            String prevIndex = bookRankingIndexSpec.dailyIndexName(referenceDate.minusDays(1));
            String currentIndex = bookRankingIndexSpec.dailyIndexName(referenceDate);

            openSearchRepository.moveIndexAlias(
                    prevIndex,
                    currentIndex,
                    bookRankingIndexSpec.dailyAliasName()
            );

            openSearchRepository.delete(prevIndex);
            return RepeatStatus.FINISHED;
        };
    }
}
