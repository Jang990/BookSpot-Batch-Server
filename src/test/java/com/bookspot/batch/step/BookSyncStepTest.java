package com.bookspot.batch.step;

import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class BookSyncStepTest {
    @Autowired JobRepository jobRepository;
    @Autowired IsbnSet isbnSet;

    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired JobLauncher jobLauncher;

    @Autowired
    Step bookSyncStep;

    private final JobParameters jobParameters = new JobParametersBuilder()
            .addString("filePath", "src/test/resources/test/stock_test.csv")
            .toJobParameters();

    @AfterEach
    void afterEach() {
        isbnSet.clearAll();
    }

    @Test
    void IsbnSet에_저장되지_않은_모든_ISBN정보를_저장한다() throws Exception {
        isbnSet.add("0000000000001");
        isbnSet.add("0000000000003");
        isbnSet.add("0000000000005");
        JobExecution jobExecution = jobLauncher.run(StepTestUtils.wrapping(bookSyncStep, jobRepository), jobParameters);

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        assertThat(List.of("0000000000002", "0000000000004"))
                .containsExactlyInAnyOrderElementsOf(
                        jdbcTemplate.queryForList("""
                            SELECT isbn13
                            FROM book
                        """, String.class));
    }
}