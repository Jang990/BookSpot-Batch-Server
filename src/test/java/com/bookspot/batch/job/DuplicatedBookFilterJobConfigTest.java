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
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@BatchJobTest
class DuplicatedBookFilterJobConfigTest {
    final String SOURCE_DIR = "src/test/resources/files/stockFilteredJob";
    final String OUTPUT_DIR = "src/test/resources/files/stockFilteredJob/result";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    Job duplicatedBookFilterJob;

    @BeforeEach
    void beforeEach() throws IOException {
        TestInsertUtils.bookBuilder().id(101L).isbn13("0000000000101").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(102L).isbn13("0000000000102").insert(jdbcTemplate);

        TestFileUtil.copy(
                "src/test/resources/files/sample/normalized/sample_normalized.csv",
                SOURCE_DIR+"/10001_2025-03-01.csv"
        );
    }

    @AfterEach
    void afterEach() throws IOException {
        TestFileUtil.deleteAll(SOURCE_DIR);
        TestFileUtil.deleteAll(OUTPUT_DIR);
    }

    @Test
    void test() throws Exception {
        jobLauncherTestUtils.setJob(duplicatedBookFilterJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                new JobParametersBuilder()
                        .addString(
                                DuplicatedBookFilterJobConfig.SOURCE_DIR_PARAM_NAME,
                                SOURCE_DIR
                        )
                        .addString(
                                DuplicatedBookFilterJobConfig.OUTPUT_DIR_PARAM_NAME,
                                OUTPUT_DIR
                        )
                        .toJobParameters()
        );

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        assertTrue(Files.exists(Path.of(OUTPUT_DIR.concat("/10001_2025-03-01_filtered.csv"))));

        StockNormalizedFileReader fileReader = new StockNormalizedFileReader(
                new FileSystemResource(
                        OUTPUT_DIR.concat("/10001_2025-03-01_filtered.csv")
                )
        );
        fileReader.open(new ExecutionContext());

        assertLine(fileReader.read(), 1,1);
        assertLine(fileReader.read(), 2,1);
        assertLine(fileReader.read(), 4,1);
        assertLine(fileReader.read(), 3,1);
        assertNull(fileReader.read());
    }

    private static void assertLine(LibraryStock line, long bookId, long libraryId) {
        assertEquals(line.getBookId(), bookId);
        assertEquals(line.getLibraryId(), libraryId);
    }
}