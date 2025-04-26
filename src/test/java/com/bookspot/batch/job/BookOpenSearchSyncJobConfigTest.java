package com.bookspot.batch.job;

import com.bookspot.batch.TestInsertUtils;
import com.bookspot.batch.global.config.OpenSearchIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@BatchJobTest
class BookOpenSearchSyncJobConfigTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    Job bookOpenSearchSyncJob;

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @MockBean
    OpenSearchIndex openSearchIndex;

    private final String INDEX_NAME = "test-books";

    @BeforeEach
    void beforeEach() {
        when(openSearchIndex.indexName()).thenReturn(INDEX_NAME);

        TestInsertUtils.bookBuilder().id(1).insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(2).insert(jdbcTemplate);

        TestInsertUtils.libraryBuilder().id(1).insert(jdbcTemplate);
        TestInsertUtils.libraryBuilder().id(2).insert(jdbcTemplate);
        TestInsertUtils.libraryBuilder().id(3).insert(jdbcTemplate);

        TestInsertUtils.libraryStockBuilder()
                .bookId(1).libraryId(1).insert(jdbcTemplate);
        TestInsertUtils.libraryStockBuilder()
                .bookId(1).libraryId(2).insert(jdbcTemplate);

        TestInsertUtils.libraryStockBuilder()
                .bookId(2).libraryId(2).insert(jdbcTemplate);
        TestInsertUtils.libraryStockBuilder()
                .bookId(2).libraryId(3).insert(jdbcTemplate);
    }

    @Test
    void 정상처리() throws Exception {
        jobLauncherTestUtils.setJob(bookOpenSearchSyncJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                new JobParametersBuilder().toJobParameters()
        );

        assertEquals(jobExecution.getExitStatus(), ExitStatus.COMPLETED);
    }
}