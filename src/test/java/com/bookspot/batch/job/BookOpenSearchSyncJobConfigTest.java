package com.bookspot.batch.job;

import com.bookspot.batch.TestInsertUtils;
import com.bookspot.batch.infra.opensearch.BookIndexSpec;
import com.bookspot.batch.infra.opensearch.IndexSpecCreator;
import com.bookspot.batch.infra.opensearch.OpenSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;

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
    IndexSpecCreator indexSpecCreator;

    @Autowired
    OpenSearchRepository repository;

    @Mock
    BookIndexSpec mockBookIndexSpec;

    private final String SERVICE_ALIAS = "test-books";
    private final String SERVICE_INDEX = "test-books-service";
    private final String DELETABLE_INDEX = "test-books-deletable";
    private final String BACKUP_INDEX = "test-books-backup";

    @BeforeEach
    void beforeEach() {
        deleteIfExist(SERVICE_INDEX);
        deleteIfExist(BACKUP_INDEX);
        deleteIfExist(DELETABLE_INDEX);

        when(indexSpecCreator.create(any())).thenReturn(mockBookIndexSpec);

        when(mockBookIndexSpec.serviceAlias()).thenReturn(SERVICE_ALIAS);
        when(mockBookIndexSpec.serviceIndexName()).thenReturn(SERVICE_INDEX);
        when(mockBookIndexSpec.backupIndexName()).thenReturn(BACKUP_INDEX);
        when(mockBookIndexSpec.deletableIndexName()).thenReturn(DELETABLE_INDEX);

        createIndexIfExist(BACKUP_INDEX);
        createIndexIfExist(DELETABLE_INDEX);
        repository.addAlias(BACKUP_INDEX, SERVICE_ALIAS);

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

    private void deleteIfExist(String indexName) {
        try {
            repository.delete(indexName);
        } catch (OpenSearchException e) {}
    }

    @Test
    void 정상처리() throws Exception {
        jobLauncherTestUtils.setJob(bookOpenSearchSyncJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                new JobParametersBuilder()
                        .addLocalDate(BookSpotParentJobConfig.MONTH_PARAM_NAME, LocalDate.now())
                        .toJobParameters()
        );

        assertEquals(jobExecution.getExitStatus(), ExitStatus.COMPLETED);
    }

    private void createIndexIfExist(String indexName) {
        try {
            repository.createIndex(indexName, BookIndexSpec.SCHEMA);
        } catch (RuntimeException e) {}
    }
}