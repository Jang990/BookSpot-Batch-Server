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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@BatchJobTest
class Temp_StockSyncJobConfigTest {
    final String SOURCE_DIR = "src/test/resources/files/stockSync"; // {1,3,5} = {1,2,4}
    final String INSERT_DIR = "src/test/resources/files/stockSync/insert";
    final String DELETE_DIR = "src/test/resources/files/stockSync/delete";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    Job temp_stockSyncJob;

    @BeforeEach
    void beforeEach() throws IOException {
        TestInsertUtils.libraryBuilder().id(1001L).insert(jdbcTemplate);
        TestInsertUtils.libraryBuilder().id(1002L).insert(jdbcTemplate);

        TestInsertUtils.bookBuilder().id(1001L).insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(1002L).insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(1003L).insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(1004L).insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(1005L).insert(jdbcTemplate);

        TestInsertUtils.libraryStockBuilder().libraryId(1001L).bookId(1001L).insert(jdbcTemplate);
        TestInsertUtils.libraryStockBuilder().libraryId(1001L).bookId(1002L).insert(jdbcTemplate);
        TestInsertUtils.libraryStockBuilder().libraryId(1001L).bookId(1003L).insert(jdbcTemplate);
        TestInsertUtils.libraryStockBuilder().libraryId(1001L).bookId(1004L).insert(jdbcTemplate);

        TestInsertUtils.libraryStockBuilder().libraryId(1002L).bookId(1001L).insert(jdbcTemplate);
        TestInsertUtils.libraryStockBuilder().libraryId(1002L).bookId(1002L).insert(jdbcTemplate);
        TestInsertUtils.libraryStockBuilder().libraryId(1002L).bookId(1003L).insert(jdbcTemplate);

        TestFileUtil.copyAll(
                "src/test/resources/files/sample/filtered/sync",
                SOURCE_DIR
        );
    }

    @AfterEach
    void afterEach() throws IOException {
        TestFileUtil.deleteAll(SOURCE_DIR);
        TestFileUtil.deleteAll(INSERT_DIR);
        TestFileUtil.deleteAll(DELETE_DIR);
    }

    @Test
    void test() throws Exception {
        jobLauncherTestUtils.setJob(temp_stockSyncJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                new JobParametersBuilder()
                        .addString(
                                Temp_StockSyncJobConfig.SOURCE_DIR_PARAM_NAME,
                                SOURCE_DIR
                        )
                        .addString(
                                Temp_StockSyncJobConfig.INSERT_DIR_PARAM_NAME,
                                INSERT_DIR
                        )
                        .addString(
                                Temp_StockSyncJobConfig.DELETE_DIR_PARAM_NAME,
                                DELETE_DIR
                        )
                        .toJobParameters()
        );

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

        assertResultFile(
                INSERT_DIR.concat("/1001_2025-03-01_insert.csv"),
                new MyResultSet(1005, 1001)
        );

        assertResultFile(
                DELETE_DIR.concat("/1001_2025-03-01_delete.csv"),
                new MyResultSet(1002, 1001),
                new MyResultSet(1004, 1001)
        );


        assertResultFile(
                INSERT_DIR.concat("/1002_2025-03-01_insert.csv"),
                new MyResultSet(1004, 1002)
        );
        assertResultFile(
                DELETE_DIR.concat("/1002_2025-03-01_delete.csv"),
                new MyResultSet(1003, 1002)
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