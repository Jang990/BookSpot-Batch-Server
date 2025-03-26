package com.bookspot.batch.job;

import com.bookspot.batch.TestFileUtil;
import com.bookspot.batch.TestInsertUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@BatchJobTest
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
        TestInsertUtils.bookBuilder().id(104L).isbn13("0000000000104").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(105L).isbn13("0000000000105").insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(106L).isbn13("0000000000106").insert(jdbcTemplate);
    }

    @Test
    void 정상_처리() throws Exception {
        jobLauncherTestUtils.setJob(stockSyncJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

        Map<Long, List<Long>> libraryIdAndBooksId = findStocks().stream()
                .collect(Collectors.groupingBy(
                        StockResult::getLibraryId,
                        Collectors.mapping(StockResult::getBookId, Collectors.toList())
                ));


        assertThat(libraryIdAndBooksId.get(10001L))
                .containsExactlyInAnyOrderElementsOf(List.of(101L, 102L));
        assertThat(libraryIdAndBooksId.get(10002L))
                .containsExactlyInAnyOrderElementsOf(List.of(104L, 105L, 106L));
        assertThat(libraryIdAndBooksId.get(10003L))
                .containsExactlyInAnyOrderElementsOf(List.of(101L, 103L, 105L));
    }

    private List<StockResult> findStocks() {
        return jdbcTemplate.query("""
                        SELECT library_id, book_id
                        FROM library_stock
                        """,
                (rs, rowNum) -> new StockResult(
                        rs.getLong("library_id"),
                        rs.getLong("book_id")
                )
        );
    }

    static class StockResult {
        private Long libraryId;
        private Long bookId;

        public StockResult(Long libraryId, Long bookId) {
            this.libraryId = libraryId;
            this.bookId = bookId;
        }

        public Long getLibraryId() {
            return libraryId;
        }

        public Long getBookId() {
            return bookId;
        }
    }
}