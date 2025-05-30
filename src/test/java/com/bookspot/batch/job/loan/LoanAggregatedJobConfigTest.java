package com.bookspot.batch.job.loan;

import com.bookspot.batch.TestInsertUtils;
import com.bookspot.batch.data.file.csv.AggregatedBook;
import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.job.BatchJobTest;
import com.bookspot.batch.step.reader.AggregatedLoanFileReader;
import com.bookspot.batch.step.service.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@BatchJobTest
class LoanAggregatedJobConfigTest {
    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    Job loanAggregatedJob;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    BookRepository bookRepository;

    String sourceDirectory = "src/test/resources/files/loanSync";
    String outputFilePath = "src/test/resources/files/loanSync/result/aggregated.csv";

    @BeforeEach
    void setup() {
        TestInsertUtils.bookBuilder().id(1001L).isbn13("0000000001001").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(1002L).isbn13("0000000001002").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(1003L).isbn13("0000000001003").insert(jdbcTemplate);
    }

    @AfterEach
    void afterEach() throws IOException {
        Files.delete(Path.of(outputFilePath));
    }

    @Test
    void 정상_처리() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString(LoanAggregatedJobConfig.DIRECTORY_PARAM_NAME, sourceDirectory)
                .addString(LoanAggregatedJobConfig.OUTPUT_FILE_PARAM_NAME, outputFilePath)
                .toJobParameters();

        jobLauncherTestUtils.setJob(loanAggregatedJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        assertFile(
                List.of(
                        new AggregatedBook("0000000001001", 123),
                        new AggregatedBook("0000000001002", 58),
                        new AggregatedBook("0000000001003", 1512)
                ),
                outputFilePath
        );
        assertTrue(Files.exists(Path.of(outputFilePath)));

        assertEquals(123, findBook(1001L).getLoanCount());
        assertEquals(58, findBook(1002L).getLoanCount());
        assertEquals(1512, findBook(1003L).getLoanCount());
    }

    private void assertFile(List<AggregatedBook> expected, String filePath) throws Exception {
        AggregatedLoanFileReader reader = new AggregatedLoanFileReader(filePath);
        reader.open(new ExecutionContext());

        for (AggregatedBook expectedInfo : expected) {
            AggregatedBook actual = reader.read();
            assertEquals(expectedInfo.isbn13(), actual.isbn13());
            assertEquals(expectedInfo.loanCount(), actual.loanCount());
        }
    }

    private ConvertedUniqueBook findBook(long id) {
        return bookRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }
}