package com.bookspot.batch.job.stock;

import com.bookspot.batch.TestFileUtil;
import com.bookspot.batch.TestQueryUtil;
import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.job.BatchJobTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@BatchJobTest
class StockSyncJobConfigTest {
    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    Job stockSyncJob;

    @Autowired
    JdbcTemplate jdbcTemplate;

    final String SOURCE_DIR = "src/test/resources/files/stockSync";

    @BeforeEach
    void beforeEach() throws IOException {
        TestFileUtil.copyAll(
                "src/test/resources/files/sample/filtered",
                SOURCE_DIR
        );
    }

    @AfterEach
    void afterEach() throws IOException {
        jdbcTemplate.execute(
                "DROP TABLE IF EXISTS %s".formatted(
                        StockSyncJobConfig.TEMP_DB_NAME
                )
        );

        TestFileUtil.deleteAll(SOURCE_DIR);
    }

    @Test
    void 정상처리() throws Exception {
        jobLauncherTestUtils.setJob(stockSyncJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                new JobParametersBuilder()
                        .addString(
                                StockSyncJobConfig.SOURCE_DIR_PARAM_NAME,
                                SOURCE_DIR
                        )
                        .addLocalDate(
                                "tempParam",
                                LocalDate.now()
                        )
                        .toJobParameters()
        );

        List<LibraryStock> stocks = TestQueryUtil.findStocks(jdbcTemplate);
    }
}