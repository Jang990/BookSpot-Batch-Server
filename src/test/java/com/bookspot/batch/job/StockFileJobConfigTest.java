package com.bookspot.batch.job;

import com.bookspot.batch.data.LibraryForFileParsing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@BatchJobTest
class StockFileJobConfigTest {
    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    Job stockFileJob;

    @MockBean
    JdbcPagingItemReader<LibraryForFileParsing> reader;

    @BeforeEach
    void setup() throws Exception {
        when(reader.read()).thenReturn(
                new LibraryForFileParsing(1L, "127058", "29981", LocalDate.of(2024, 12, 1)),
                null
        );
    }

//    @Test
    void 정상처리() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString(StockFileJobConfig.DOWNLOAD_DIR_PARAM_NAME, "src/test/resources/files/stockFileJob")
                .toJobParameters();

        jobLauncherTestUtils.setJob(stockFileJob);
        jobLauncherTestUtils.launchJob(jobParameters);

        // test 주석을 해제하려면 assert문 필요
    }
}