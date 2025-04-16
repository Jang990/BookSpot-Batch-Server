package com.bookspot.batch.job.stock;

import com.bookspot.batch.TestFileUtil;
import com.bookspot.batch.TestInsertUtils;
import com.bookspot.batch.TestQueryUtil;
import com.bookspot.batch.data.Library;
import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.job.BatchJobTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    EntityManager em;

    final String TARGET_DIR = "src/test/resources/files/stockSync";
    final JobParameters parameters = new JobParametersBuilder()
            .addString(StockSyncJobConfig.SOURCE_DIR_PARAM_NAME, TARGET_DIR)
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

        assertEmptyDirectory(TARGET_DIR);
        assertLibraryStockUpdatedAt();
        assertStockData();
    }

    private void assertEmptyDirectory(String directory) throws IOException {
        Path dirPath = Path.of(directory);

        try (Stream<Path> files = Files.list(dirPath)) {
            assertTrue(files.findAny().isEmpty());
        } catch (IOException e) {
            throw e;
        }
    }

    private void assertStockData() {
        Map<Long, List<Long>> libraryIdAndBooksId = TestQueryUtil.findStocks(jdbcTemplate).stream()
                .collect(Collectors.groupingBy(
                        LibraryStock::getLibraryId,
                        Collectors.mapping(LibraryStock::getBookId, Collectors.toList())
                ));

        assertThat(libraryIdAndBooksId.get(10001L))
                .containsExactlyInAnyOrderElementsOf(List.of(101L, 102L));
        assertThat(libraryIdAndBooksId.get(10002L))
                .containsExactlyInAnyOrderElementsOf(List.of(104L, 105L, 106L));
        assertThat(libraryIdAndBooksId.get(10003L))
                .containsExactlyInAnyOrderElementsOf(List.of(101L, 103L, 105L));
    }

    private void assertLibraryStockUpdatedAt() {
        List<Library> libraries = TestQueryUtil.findLibrariesByIds(em, List.of(10001L, 10002L, 10003L));

        for (Library library : libraries)
            assertEquals(LocalDate.of(2025, 3, 1), library.getStockUpdatedAt());
    }
}