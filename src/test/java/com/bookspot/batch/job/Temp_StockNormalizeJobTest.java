package com.bookspot.batch.job;

import com.bookspot.batch.TestFileUtil;
import com.bookspot.batch.TestInsertUtils;
import com.bookspot.batch.data.LibraryStock;
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
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@BatchJobTest
class Temp_StockNormalizeJobTest {
    final String SOURCE_DIR = "src/test/resources/files/stockNormalize";
    final String OUTPUT_DIR = "src/test/resources/files/stockNormalize/result";

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
        TestInsertUtils.bookBuilder().id(104L).isbn13("0000000000104").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(105L).isbn13("0000000000105").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(106L).isbn13("0000000000106").insert(jdbcTemplate);

        TestFileUtil.copy(
                "src/test/resources/files/sample/stockSync/10001_2025-03-01.csv",
                SOURCE_DIR+"/10001_2025-03-01.csv"
        );
    }

    @AfterEach
    void afterEach() throws IOException {
        TestFileUtil.deleteAll(OUTPUT_DIR);
    }

    @Test
    void test() throws Exception {
        jobLauncherTestUtils.setJob(stockNormalizeJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                new JobParametersBuilder()
                        .addString(
                                Temp_StockNormalizeJob.SOURCE_DIR_PARAM_NAME,
                                SOURCE_DIR
                        )
                        .addString(
                                Temp_StockNormalizeJob.NORMALIZE_DIR_PARAM_NAME,
                                OUTPUT_DIR
                        )
                        .toJobParameters()
        );

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        assertTrue(Files.exists(Path.of(OUTPUT_DIR.concat("/10001_2025-03-01_normalized.csv"))));

        StockNormalizedFileReader fileReader = new StockNormalizedFileReader(OUTPUT_DIR.concat("/10001_2025-03-01_normalized.csv"));
        fileReader.open(new ExecutionContext());

        assertLine(fileReader.read(), 101, 10001);
        assertLine(fileReader.read(), 102, 10001);
        assertNull(fileReader.read());
    }

    private static void assertLine(LibraryStock line, long bookId, long libraryId) {
        assertEquals(line.getBookId(), bookId);
        assertEquals(line.getLibraryId(), libraryId);
    }

}