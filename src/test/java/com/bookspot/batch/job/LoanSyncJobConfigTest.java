package com.bookspot.batch.job;

import com.bookspot.batch.TestInsertUtils;
import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.job.validator.FilePathJobParameterValidator;
import com.bookspot.batch.step.service.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@BatchJobTest
class LoanSyncJobConfigTest {
    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    Job loanSyncJob;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    BookRepository bookRepository;

    void setup() {
        TestInsertUtils.bookBuilder().id(1001L).isbn13("0000000001001").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(1002L).isbn13("0000000001002").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(1003L).isbn13("0000000001003").insert(jdbcTemplate);
    }

    @Test
    void 정상_처리() throws Exception {
        setup();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(LoanSyncJobConfig.DIRECTORY_PARAM_NAME, "src/test/resources/files/loanSync")
                .addString(LoanSyncJobConfig.AGGREGATED_FILE_PARAM_NAME, "src/test/resources/files/loanSync/aggregated/aggregated.csv")
                .toJobParameters();

        jobLauncherTestUtils.setJob(loanSyncJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

        assertEquals(123, findBook(1001L).getLoanCount());
        assertEquals(58, findBook(1002L).getLoanCount());
        assertEquals(1512, findBook(1003L).getLoanCount());
    }

    private ConvertedUniqueBook findBook(long id) {
        return bookRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }
}