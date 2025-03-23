package com.bookspot.batch.job;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

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

    final JobParameters parameters = new JobParametersBuilder()
            .addString("rootDirPath", "src/test/resources/test/books")
            .toJobParameters();

    @Test
    void 정상_처리() throws Exception {
        insertLibrary(10001L);
        insertLibrary(10002L);
        insertLibrary(10003L);

        // TODO: 도서 정보 insert 필요

        jobLauncherTestUtils.setJob(stockSyncJob);
        jobLauncherTestUtils.launchJob(parameters);
    }

    private void insertLibrary(long id) {
        jdbcTemplate.execute("""
                INSERT INTO LIBRARY (id, library_code, location, name)
                VALUES
                (%d, '%d', ST_GeomFromText('POINT(%d %d)', 4326), '%s')
                """.formatted(id, id, 34, 34, id + "번 도서관"));
    }
}