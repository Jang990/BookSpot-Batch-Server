package com.bookspot.batch.job.temp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
class TempPartitionJobConfigTest {
    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    Job tempBookSyncPartitionedJob;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void test() throws Exception {
        jobLauncherTestUtils.setJob(tempBookSyncPartitionedJob);
        jobLauncherTestUtils.launchJob(new JobParametersBuilder()
                .addString("rootDirPath", "src/test/resources/test/books")
                .toJobParameters()
        );

        assertThat(List.of("0000000000001", "0000000000002", "0000000000003", "0000000000004", "0000000000005"))
                .containsExactlyInAnyOrderElementsOf(
                        jdbcTemplate.queryForList("""
                            SELECT isbn13
                            FROM book
                        """, String.class));
    }
}