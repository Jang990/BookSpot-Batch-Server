package com.bookspot.batch.job.stock;

import com.bookspot.batch.TestFileUtil;
import com.bookspot.batch.TestInsertUtils;
import com.bookspot.batch.TestQueryUtil;
import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.global.FileService;
import com.bookspot.batch.job.BatchJobTest;
import com.bookspot.batch.step.reader.CleansingStockFileReader;
import org.assertj.core.api.Assertions;
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
    final String CLEANSING_DIR = "src/test/resources/files/stockSync/cleansing";
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
        TestInsertUtils.bookBuilder().id(101L).isbn13("0000000000101").subjectCode("813.8").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(102L).isbn13("0000000000102").subjectCode("813.8").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(103L).isbn13("0000000000103").subjectCode("813.8").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(104L).isbn13("0000000000104").subjectCode("813.8").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(105L).isbn13("0000000000105").subjectCode("813.8").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(106L).isbn13("0000000000106").subjectCode("813.8").insert(jdbcTemplate);

        TestInsertUtils.libraryBuilder().id(10002L).insert(jdbcTemplate);

        TestInsertUtils.libraryStockBuilder().libraryId(10002L).bookId(101L).insert(jdbcTemplate);
        TestInsertUtils.libraryStockBuilder().libraryId(10002L).bookId(102L).insert(jdbcTemplate);
        TestInsertUtils.libraryStockBuilder().libraryId(10002L).bookId(103L).insert(jdbcTemplate);

        TestInsertUtils.libraryStockBuilder().libraryId(10002L).bookId(104L).subjectCode("123.123").insert(jdbcTemplate);

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
        TestFileUtil.deleteAll(CLEANSING_DIR);
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
                                StockSyncJobConfig.CLEANSING_DIR_PARAM_NAME,
                                CLEANSING_DIR
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
        assertStockData(10002, 104, "123.123"); // 새로운 Insert가 아니라면 기존 데이터 유지
        assertStockData(10002, 105, "510.4");
        assertStockData(10002, 106, "510.4");
        assertEquals(3, TestQueryUtil.findStocks(jdbcTemplate, 10002).size());
    }

    private void assertStockData(long libraryId, long bookId, String subjectCode) {
        LibraryStock stock = TestQueryUtil.findSingleStock(jdbcTemplate, libraryId, bookId);
        assertEquals(libraryId, stock.getLibraryId());
        assertEquals(bookId, stock.getBookId());
        assertEquals(subjectCode, stock.getSubjectCode());
    }

    private void assertResultFile() throws Exception {
        assertResultFile(
                CLEANSING_DIR.concat("/10002_2025-03-01_cleansing.csv"),
                new MyResultSet(104, 10002, "813.8"),
                new MyResultSet(105, 10002, "510.4"),
                new MyResultSet(106, 10002, "510.4"),
                new MyResultSet(105, 10002, "510.4")
        );

        assertResultFile(
                FILTERED_DIR.concat("/10002_2025-03-01_filtered.csv"),
                new MyResultSet(104, 10002, "813.8"),
                new MyResultSet(105, 10002, "510.4"),
                new MyResultSet(106, 10002, "510.4")
        );

        /**
         * @see com.bookspot.batch.step.listener.DeletedStockFileCreator
         * delete시에는 분류번호는 필요없는 값이기 때문에 null로 설정됨
         */
        assertResultFile(
                DELETE_DIR.concat("/10002_2025-03-01_delete.csv"),
                new MyResultSet(101, 10002, null),
                new MyResultSet(103, 10002, null),
                new MyResultSet(102, 10002, null)
        );
    }

    private static void assertResultFile(String resultPath, MyResultSet... resultSets) throws Exception {
        assertTrue(Files.exists(Path.of(resultPath)));

        CleansingStockFileReader fileReader = new CleansingStockFileReader(
                new FileSystemResource(
                        resultPath
                )
        );
        fileReader.open(new ExecutionContext());

        for (MyResultSet resultSet : resultSets)
            assertLine(fileReader.read(), resultSet.bookId, resultSet.libraryId, resultSet.subjectCode);
        assertNull(fileReader.read());
    }

    private static void assertLine(LibraryStock line, long bookId, long libraryId, String subjectCode) {
        assertEquals(line.getBookId(), bookId);
        assertEquals(line.getLibraryId(), libraryId);
        assertEquals(line.getSubjectCode(), subjectCode);
    }

    record MyResultSet(long bookId, long libraryId, String subjectCode) {}
}