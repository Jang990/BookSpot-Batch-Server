package com.bookspot.batch.job.stock;

import com.bookspot.batch.TestFileUtil;
import com.bookspot.batch.TestInsertUtils;
import com.bookspot.batch.TestQueryUtil;
import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.global.FileService;
import com.bookspot.batch.job.BatchJobTest;
import com.bookspot.batch.step.reader.StockNormalizedFileReader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@BatchJobTest
class StockSyncJobConfigTest {
    final String SOURCE_DIR = "src/test/resources/files/stockSync";
    final String NORMALIZED_DIR = "src/test/resources/files/stockSync/normalized";
    final String FILTERED_DIR = "src/test/resources/files/stockSync/filtered";
    final String DELETE_DIR = "src/test/resources/files/stockSync/delete";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    Job stockSyncJob;

    @SpyBean
    FileService fileService;

    @BeforeEach
    void beforeEach() throws IOException {
        TestInsertUtils.bookBuilder().id(101L).isbn13("0000000000101").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(102L).isbn13("0000000000102").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(103L).isbn13("0000000000103").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(104L).isbn13("0000000000104").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(105L).isbn13("0000000000105").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(106L).isbn13("0000000000106").insert(jdbcTemplate);

        TestInsertUtils.libraryBuilder().id(10002L).insert(jdbcTemplate);

        TestInsertUtils.libraryStockBuilder().libraryId(10002L).bookId(101L).insert(jdbcTemplate);
        TestInsertUtils.libraryStockBuilder().libraryId(10002L).bookId(102L).insert(jdbcTemplate);
        TestInsertUtils.libraryStockBuilder().libraryId(10002L).bookId(103L).insert(jdbcTemplate);

        TestInsertUtils.libraryStockBuilder().libraryId(10002L).bookId(104L).insert(jdbcTemplate);

        /*
        4,5,6
         */

        TestFileUtil.copy(
                "src/test/resources/files/sample/stock/10002_2025-03-01.csv",
                SOURCE_DIR.concat("/10002_2025-03-01.csv")
        );
    }

    @AfterEach
    void afterEach() throws IOException {
        TestFileUtil.deleteAll(SOURCE_DIR);
        TestFileUtil.deleteAll(NORMALIZED_DIR);
        TestFileUtil.deleteAll(FILTERED_DIR);
        TestFileUtil.deleteAll(DELETE_DIR);
    }

    @Test
    void test() throws Exception {
        doNothing().when(fileService).delete(any());

        jobLauncherTestUtils.setJob(stockSyncJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                new JobParametersBuilder()
                        .addString(
                                StockSyncJobConfig.SOURCE_DIR_PARAM_NAME,
                                SOURCE_DIR
                        )
                        .addString(
                                StockSyncJobConfig.NORMALIZE_DIR_PARAM_NAME,
                                NORMALIZED_DIR
                        )
                        .addString(
                                StockSyncJobConfig.DUPLICATED_FILTER_DIR_PARAM_NAME,
                                FILTERED_DIR
                        )
                        .addString(
                                StockSyncJobConfig.DELETE_DIR_PARAM_NAME,
                                DELETE_DIR
                        )
                        .toJobParameters()
        );

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

        assertResultFile();
        assertStockData(10002, List.of(104L, 105L, 106L));
    }

    private void assertStockData(long libraryId, List<Long> bookIds) {
        List<LibraryStock> stocks = TestQueryUtil.findStocks(jdbcTemplate, libraryId);
        Assertions.assertThat(bookIds)
                .containsExactlyInAnyOrderElementsOf(
                        stocks.stream()
                                .mapToLong(LibraryStock::getBookId)
                                .boxed()
                                .toList()
                );

    }

    private void assertResultFile() throws Exception {
        assertResultFile(
                NORMALIZED_DIR.concat("/10002_2025-03-01_normalized.csv"),
                new MyResultSet(104, 10002),
                new MyResultSet(105, 10002),
                new MyResultSet(106, 10002),
                new MyResultSet(105, 10002)
        );

        assertResultFile(
                FILTERED_DIR.concat("/10002_2025-03-01_filtered.csv"),
                new MyResultSet(104, 10002),
                new MyResultSet(105, 10002),
                new MyResultSet(106, 10002)
        );

        assertResultFile(
                DELETE_DIR.concat("/10002_2025-03-01_delete.csv"),
                new MyResultSet(101, 10002),
                new MyResultSet(103, 10002),
                new MyResultSet(102, 10002)
        );
    }

    private static void assertResultFile(String resultPath, MyResultSet... resultSets) throws Exception {
        assertTrue(Files.exists(Path.of(resultPath)));

        StockNormalizedFileReader fileReader = new StockNormalizedFileReader(
                new FileSystemResource(
                        resultPath
                )
        );
        fileReader.open(new ExecutionContext());

        for (MyResultSet resultSet : resultSets)
            assertLine(fileReader.read(), resultSet.bookId, resultSet.libraryId);
        assertNull(fileReader.read());
    }

    private static void assertLine(LibraryStock line, long bookId, long libraryId) {
        assertEquals(line.getBookId(), bookId);
        assertEquals(line.getLibraryId(), libraryId);
    }

    record MyResultSet(long bookId, long libraryId) {}
}