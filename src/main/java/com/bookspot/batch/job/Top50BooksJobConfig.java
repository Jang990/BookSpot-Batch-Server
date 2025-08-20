package com.bookspot.batch.job;

import com.bookspot.batch.data.Top50Book;
import com.bookspot.batch.infra.opensearch.BookRankingIndexSpec;
import com.bookspot.batch.infra.opensearch.IndexSpecCreator;
import com.bookspot.batch.infra.opensearch.OpenSearchRepository;
import com.bookspot.batch.step.book.api.Top50BookApiReader;
import com.bookspot.batch.step.book.api.Top50BookWriter;
import com.bookspot.batch.step.reader.api.top50.WeeklyTop50ApiRequester;
import com.bookspot.batch.step.service.BookCodeResolver;
import com.bookspot.batch.step.service.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class Top50BooksJobConfig {
    private static final int TOP_50 = 50;

    public static final String REFERENCE_DATE_PARAM_NAME = "referenceDate";
    public static final String REFERENCE_DATE_PARAM = "#{jobParameters['referenceDate']}";

    public static final String COND_PERIOD_PARAM_NAME = "condPeriod";
    public static final String COND_PERIOD_PARAM = "#{jobParameters['condPeriod']}";

    public static final String COND_AGE_PARAM_NAME = "condAge";
    public static final String COND_AGE_PARAM = "#{jobParameters['condAge']}";

    public static final String COND_GENDER_PARAM_NAME = "condGender";
    public static final String COND_GENDER_PARAM = "#{jobParameters['condGender']}";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final WeeklyTop50ApiRequester weeklyTop50ApiRequester;

    private final BookRepository bookRepository;
    private final OpenSearchRepository openSearchRepository;
    private final BookCodeResolver bookCodeResolver;

    private final IndexSpecCreator indexSpecCreator;

    @Bean
    @StepScope
    public Top50BookApiReader top50BookApiReader(@Value(REFERENCE_DATE_PARAM) LocalDate referenceDate) {
        return new Top50BookApiReader(referenceDate, weeklyTop50ApiRequester);
    }

    @Bean
    @StepScope
    public Top50BookWriter top50BookWriter(@Value(REFERENCE_DATE_PARAM)LocalDate referenceDate) {
        BookRankingIndexSpec bookRankingIndexSpec = indexSpecCreator.createRankingIndexSpec();
        return new Top50BookWriter(
                referenceDate,
                bookRankingIndexSpec.serviceIndexName(),
                bookRepository,
                openSearchRepository,
                bookCodeResolver
        );
    }

    @Bean
    public Step bookTop50SyncStep() {
        return new StepBuilder("bookTop50SyncStep", jobRepository)
                .<Top50Book, Top50Book>chunk(TOP_50, transactionManager)
                .reader(top50BookApiReader(null))
                .writer(top50BookWriter(null))
                .build();
    }

    // TODO: 이전 주(4주전 작업한 내용) 삭제작업
    // TODO: 추후에 나이, 성별에 따라 top 50 요청
    @Bean
    public Job top50BooksJob() {
        return new JobBuilder("top50BooksJob", jobRepository)
                .start(bookTop50SyncStep())
                .build();
    }
}
