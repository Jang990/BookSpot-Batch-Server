package com.bookspot.batch.job.stock;

import com.bookspot.batch.TestFileUtil;
import com.bookspot.batch.TestInsertUtils;
import com.bookspot.batch.TestQueryUtil;
import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.job.BatchJobTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

    final String SOURCE_DIR = "src/test/resources/files/stockSync";

    @BeforeEach
    void beforeEach() throws IOException {
        TestFileUtil.copyAll(
                "src/test/resources/files/sample/filtered",
                SOURCE_DIR
        );

        TestInsertUtils.libraryBuilder().id(1L).insert(jdbcTemplate);

        TestInsertUtils.bookBuilder().id(1L).insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(2L).insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(3L).insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(4L).insert(jdbcTemplate);

        TestInsertUtils.libraryStockBuilder()
                .bookId(1L).libraryId(1L)
                .createdAt(LocalDate.of(2000, 10, 10))
                .updatedAt(LocalDate.of(2000, 10, 10))
                .insert(jdbcTemplate);
    }

    @AfterEach
    void afterEach() throws IOException {
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

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());


        List<LibraryStock> stocks = TestQueryUtil.findStocks(jdbcTemplate);
        Map<Long, List<LibraryStock>> map = toLibraryStockMap(stocks);

        assertEquals(
                LocalDate.of(2000, 10, 10),
                find(stocks,1L, 1L).getCreatedAt()
        );

        assertEquals(
                LocalDate.now(),
                find(stocks,1L, 1L).getUpdatedAt()
        );

        /*assertThat(List.of(1L, 2L, 3L, 4L))
                .containsExactlyInAnyOrderElementsOf(
                        map.get(1L).stream()
                                .mapToLong(LibraryStock::getBookId)
                                .boxed().toList()
                );*/
    }

    private LibraryStock find(List<LibraryStock> stocks, long bookId, long libraryId) {
        return stocks.stream().filter(
                        libraryStock ->
                                libraryStock.getBookId().equals(bookId)
                                        && libraryStock.getLibraryId().equals(libraryId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private Map<Long, List<LibraryStock>> toLibraryStockMap(List<LibraryStock> stocks) {
        return stocks.stream()
                .collect(
                        Collectors.groupingBy(
                                LibraryStock::getLibraryId
                        )
                );
    }
}