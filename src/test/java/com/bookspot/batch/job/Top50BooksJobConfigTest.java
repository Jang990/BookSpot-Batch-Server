package com.bookspot.batch.job;

import com.bookspot.batch.TestInsertUtils;
import com.bookspot.batch.data.Top50Book;
import com.bookspot.batch.data.Top50BookStrings;
import com.bookspot.batch.infra.opensearch.BookRankingIndexSpec;
import com.bookspot.batch.infra.opensearch.IndexSpecCreator;
import com.bookspot.batch.infra.opensearch.OpenSearchRepository;
import com.bookspot.batch.step.reader.api.top50.WeeklyTop50ApiRequester;
import com.bookspot.batch.step.service.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@BatchJobTest
class Top50BooksJobConfigTest {
    private final String TEST_OS_WEEKLY_INDEX_NAME = "test-ranking-weekly-index";

    @Autowired
    BookRepository bookRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    Job weeklyTop50BooksJob;

    @Autowired
    OpenSearchRepository repository;

    @MockBean
    IndexSpecCreator indexSpecCreator;

    @MockBean
    WeeklyTop50ApiRequester top50ApiRequester;

    @Mock
    BookRankingIndexSpec bookRankingIndexSpec;

    @BeforeEach
    void beforeEach() {
        OpenSearchTestHelper.deleteIfExist(repository, TEST_OS_WEEKLY_INDEX_NAME);
        OpenSearchTestHelper.createIndexIfExist(repository, TEST_OS_WEEKLY_INDEX_NAME, BookRankingIndexSpec.SCHEMA);

        when(indexSpecCreator.createRankingIndexSpec()).thenReturn(bookRankingIndexSpec);
        when(bookRankingIndexSpec.serviceIndexName()).thenReturn(TEST_OS_WEEKLY_INDEX_NAME);
    }

    @Test
    void 정보나루에서_top50_책정보를_가져오고_없는내용은_DB에_저장_후_OpenSearch_최신화() throws Exception {
        TestInsertUtils.bookBuilder().id(1).isbn13("1111111111111").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(2).isbn13("2222222222222").insert(jdbcTemplate);

        when(top50ApiRequester.findTop50(any(), any())).thenReturn(
                List.of(
                        createTop50BookResponse("1", "1111111111111"),
                        createTop50BookResponse("2", "2222222222222"),
                        createTop50BookResponse("3", "3333333333333")
                )
        );


        jobLauncherTestUtils.setJob(weeklyTop50BooksJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                new JobParametersBuilder()
                        .addLocalDate(
                                Top50BooksJobConfig.REFERENCE_DATE_PARAM_NAME,
                                LocalDate.of(2025, 8, 18)
                        )
                        .toJobParameters()
        );

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        assertFalse(bookRepository.findByIsbn13In(List.of("3333333333333")).isEmpty());
    }

    private Top50BookStrings createTop50BookResponse(String rankStr, String isbn13) {
        return new Top50BookStrings(
                rankStr, "1", rankStr,
                rankStr, rankStr, isbn13,
                rankStr, rankStr, rankStr
        );
    }

}