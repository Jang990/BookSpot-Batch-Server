package com.bookspot.batch.step;

import com.bookspot.batch.step.service.Isbn13MemoryData;
import com.bookspot.batch.step.service.IsbnEclipseMemoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
class LibraryStockUpdateStepConfigTest {
    @Autowired JobRepository jobRepository;
    @Autowired IsbnEclipseMemoryRepository memoryRepository;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired JobLauncher jobLauncher;

    @Autowired
    Step libraryStockSyncStep;

    private final Long TEST_LIBRARY_ID = 1L;

    @BeforeEach
    void beforeEach() {
        for (long i = 1; i <= 5; i++) {
            System.out.println("%013d".formatted(i));
            memoryRepository.add(new Isbn13MemoryData("%013d".formatted(i), i));
        }
    }

    @AfterEach
    void afterEach() {
        memoryRepository.clearMemory();
    }

    @Test
    void 파일경로와_도서관ID를_주면_파일을_파싱해서_도서관_재고로_넣는다() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", "src/test/resources/test/stock_test.csv")
                .addLong("libraryId", TEST_LIBRARY_ID)
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(StepTestUtils.wrapping(libraryStockSyncStep, jobRepository), jobParameters);
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

        assertThat(List.of(1L, 2L, 3L, 4L, 5L))
                .containsExactlyInAnyOrderElementsOf(
                        jdbcTemplate.queryForList("""
                            SELECT book_id
                            FROM library_stock 
                            WHERE library_id = ?
                        """, Long.class, TEST_LIBRARY_ID));
    }
}