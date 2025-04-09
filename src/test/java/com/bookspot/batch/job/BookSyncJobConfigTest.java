package com.bookspot.batch.job;

import com.bookspot.batch.TestInsertUtils;
import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.step.service.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@BatchJobTest
class BookSyncJobConfigTest {
    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    Job bookSyncJob;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    BookRepository bookRepository;


    private final String sourceDir = "src/test/resources/files/booksync";
//    private final String moveDir = "src/test/resources/files/booksync/after";

    final JobParameters parameters = new JobParametersBuilder()
            .addString(BookSyncJobConfig.SOURCE_DIR_PARAM_NAME, sourceDir)
//            .addString(BookSyncJobConfig.MOVE_DIR_PARAM_NAME, moveDir)
            .toJobParameters();

    @Test
    void 정상_처리() throws Exception {
        registerExistingBooks("0000000000003");

        jobLauncherTestUtils.setJob(bookSyncJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);


        List<String> addedIsbn13 = List.of("0000000000001", "0000000000002", "0000000000004", "0000000000005");
        Map<String, ConvertedUniqueBook> bookMap = bookRepository.findByIsbn13In(addedIsbn13).stream()
                .collect(Collectors.toMap(
                        ConvertedUniqueBook::getIsbn13,
                        Function.identity()
                ));

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        assertLibrary(bookMap.get("0000000000001"), "(놀라운) 호랑이 빵집 (4)","서지원 글 ;홍그림 그림","아르볼",Year.of(2024),"0000000000001",813, LocalDate.of(2024, 11, 8));
        assertLibrary(bookMap.get("0000000000002"), "의사 어벤저스 (20)", "고희정 글;조승연 그림", "가나출판사", Year.of(2024), "0000000000002", 510, LocalDate.of(2024, 11, 8));
        assertLibrary(bookMap.get("0000000000004"), "낭만 강아지 봉봉 (7)","홍민정 글;김무연 그림","다산어린이",Year.of(2024),"0000000000004",813,LocalDate.of(2024, 11, 8));
        assertLibrary(bookMap.get("0000000000005"), "변호사 어벤저스 (3)","고희정 글;최미란 그림","가나출판사",Year.of(2024), "0000000000005",360,LocalDate.of(2024, 11, 8));

        // 파일 초기화
//        TestFileUtil.moveAll(moveDir, sourceDir);
    }

    private void assertLibrary(ConvertedUniqueBook dbBookData, String title, String author, String publisher, Year year, String isbn13, int subjectCode, LocalDate registeredDate) {
        assertEquals(title, dbBookData.getTitle());
        assertEquals(author, dbBookData.getAuthor());
        assertEquals(publisher, dbBookData.getPublisher());
        assertEquals(year, dbBookData.getPublicationYear());
        assertEquals(isbn13, dbBookData.getIsbn13());
        assertEquals(subjectCode, dbBookData.getSubjectCode());
//        assertEquals(registeredDate, ...);
    }

    private void registerExistingBooks(String... isbn13Array) {
        for (String isbn13 : isbn13Array) {
            TestInsertUtils.bookBuilder().isbn13(isbn13).insert(jdbcTemplate);
        }
    }
}