package com.bookspot.batch.job.stock;

import com.bookspot.batch.TestFileUtil;
import com.bookspot.batch.TestInsertUtils;
import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.job.BatchJobTest;
import com.bookspot.batch.step.reader.StockNormalizedFileReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@BatchJobTest
class StockNormalizeJobConfigTest {
    final String SOURCE_DIR = "src/test/resources/files/stockSync";
    final String NORMALIZED_DIR = "src/test/resources/files/stockSync/normalized";
    final String FILTERED_DIR = "src/test/resources/files/stockSync/filtered";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    Job stockNormalizeJob;

    @BeforeEach
    void beforeEach() throws IOException {
        TestInsertUtils.bookBuilder().id(101L).isbn13("0000000000101").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(102L).isbn13("0000000000102").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(103L).isbn13("0000000000103").insert(jdbcTemplate);

        TestFileUtil.copy(
                "src/test/resources/files/sample/stock/10001_2025-03-01.csv",
                SOURCE_DIR.concat("/10001_2025-03-01.csv")
        );
    }

    @AfterEach
    void afterEach() throws IOException {
        TestFileUtil.deleteAll(SOURCE_DIR);
        TestFileUtil.deleteAll(NORMALIZED_DIR);
        TestFileUtil.deleteAll(FILTERED_DIR);
    }

    @Test
    void test() throws Exception {
        jobLauncherTestUtils.setJob(stockNormalizeJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                new JobParametersBuilder()
                        .addString(
                                StockNormalizeJobConfig.SOURCE_DIR_PARAM_NAME,
                                SOURCE_DIR
                        )
                        .addString(
                                StockNormalizeJobConfig.NORMALIZE_DIR_PARAM_NAME,
                                NORMALIZED_DIR
                        )
                        .addString(
                                StockNormalizeJobConfig.DUPLICATED_FILTER_DIR_PARAM_NAME,
                                FILTERED_DIR
                        )
                        .toJobParameters()
        );

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

        assertResultFile(
                NORMALIZED_DIR.concat("/10001_2025-03-01_normalized.csv"),
                new MyResultSet(101, 10001),
                new MyResultSet(102, 10001),
                new MyResultSet(101, 10001)
        );

        assertResultFile(
                FILTERED_DIR.concat("/10001_2025-03-01_filtered.csv"),
                new MyResultSet(101, 10001),
                new MyResultSet(102, 10001)
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