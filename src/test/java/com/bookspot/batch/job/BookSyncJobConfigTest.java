package com.bookspot.batch.job;

import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
class BookSyncJobConfigTest {
    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    Job bookSyncPartitionedJob;

    @Autowired
    JdbcTemplate jdbcTemplate;


    final JobParameters parameters = new JobParametersBuilder()
            .addString("rootDirPath", "src/test/resources/files/booksync")
            .toJobParameters();

    @Test
    void 정상_처리() throws Exception {
        insertBook("0000000000001");
        insertBook("0000000000003");
        insertBook("0000000000005");

        jobLauncherTestUtils.setJob(bookSyncPartitionedJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);

        // TODO: 책 상세정보 검증
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        assertThat(List.of("0000000000002", "0000000000004"))
                .containsExactlyInAnyOrderElementsOf(
                        jdbcTemplate.queryForList("""
                            SELECT isbn13
                            FROM book
                            WHERE subject_code IS NOT NULL
                        """, String.class)); // 임시 insert된 데이터는 subjectCode가 없음
    }

    private void insertBook(String isbn) {
        jdbcTemplate.execute("INSERT INTO book(isbn13) VALUES('%s')".formatted(isbn));
    }
}