package com.bookspot.batch.job;

import com.bookspot.batch.TestFileUtil;
import com.bookspot.batch.TestInsertUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
class StockSyncJobConfigTest {
    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    Job stockSyncJob;

    @Autowired
    JdbcTemplate jdbcTemplate;

    final String TARGET_DIR = "src/test/resources/files/stockSync";
    final JobParameters parameters = new JobParametersBuilder()
            .addString("rootDirPath", TARGET_DIR)
            .toJobParameters();

    @BeforeEach
    void setup() throws IOException {
        TestFileUtil.copyAll("src/test/resources/files/sample/stockSync", TARGET_DIR);

        TestInsertUtils.libraryBuilder().id(10001L).insert(jdbcTemplate);
        TestInsertUtils.libraryBuilder().id(10002L).insert(jdbcTemplate);
        TestInsertUtils.libraryBuilder().id(10003L).insert(jdbcTemplate);

        TestInsertUtils.bookBuilder().id(101L).isbn13("0000000000101").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(102L).isbn13("0000000000102").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(103L).isbn13("0000000000103").insert(jdbcTemplate);
    }

    @Test
    void 정상_처리() throws Exception {
        jobLauncherTestUtils.setJob(stockSyncJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }
}