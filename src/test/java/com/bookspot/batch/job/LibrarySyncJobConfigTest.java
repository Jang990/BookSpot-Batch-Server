package com.bookspot.batch.job;

import com.bookspot.batch.data.Library;
import com.bookspot.batch.data.crawler.LibraryNaruDetail;
import com.bookspot.batch.step.reader.LibraryExcelConst;
import com.bookspot.batch.step.reader.LibraryNaruDetailReader;
import com.bookspot.batch.step.reader.file.excel.library.LibraryFileDownloader;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
class LibrarySyncJobConfigTest {

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    Job librarySyncJob;

    @Autowired
    EntityManager entityManager;

    @MockBean
    LibraryFileDownloader libraryFileDownloader;

    @MockBean
    LibraryNaruDetailReader libraryNaruDetailReader;


    private static final Path SAMPLE_FILE = Path.of("src/test/resources/test/library_list.xlsx");
    private static final Path DEST_FILE = Path.of(LibraryExcelConst.metadata.absolutePath()); // 복사 대상

    @BeforeEach
    void setUp() throws Exception {
        changeDownloadingSampleFile();

        when(libraryNaruDetailReader.read()).thenReturn(
                new LibraryNaruDetail("2.28도서관", "대구광역시 중구 2·28길 9", "29981"),
                new LibraryNaruDetail("KB국민은행과 함께하는 나무 작은도서관", "서울특별시 노원구 동일로 1405", "25937"),
                new LibraryNaruDetail("U보라작은도서관",	"경상남도 김해시 전하로176번길 71, 반도보라아파트 주민공동시설 2층", "8504"),
                null
        );

        jobLauncherTestUtils.setJob(librarySyncJob);
    }

    private void changeDownloadingSampleFile() throws IOException {
        if(!Files.exists(SAMPLE_FILE))
            fail("테스트할 샘플 도서관 정보 엑셀 파일이 존재하지 않음");

        // 대상 디렉토리가 없으면 생성
        Files.createDirectories(DEST_FILE.getParent());

        // 테스트 최적화를 위해 일부 도서관 정보만 있는 파일로 바꿔치기
        doAnswer((invocationOnMock ->  {
            Files.copy(SAMPLE_FILE, DEST_FILE, StandardCopyOption.REPLACE_EXISTING);
            return invocationOnMock;
        })).when(libraryFileDownloader).download();
    }

    // TODO: org.springframework.dao.EmptyResultDataAccessException: Item 0 of 3 did not update any rows: [LibraryNaruDetail[name=2.28도서관, address=대구광역시 중구 2·28길 9, naruDetail=29981]] 스킵해도 되는가?
    @Test
    void 정상처리() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(new JobParametersBuilder()
                .addLocalDateTime("temp_date", LocalDateTime.now())
                .toJobParameters());

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        Map<String, Library> libraryMap = toMap(findLibraries());

        assertLibrary(libraryMap.get("127058"), "127058", "2.28도서관", "대구광역시 중구 2·28길 9", 128.5894055, 35.8592504);
        assertLibrary(libraryMap.get("148096"), "148096", "U보라작은도서관", "경상남도 김해시 전하로176번길 71, 반도보라아파트 주민공동시설 2층", 128.874226, 35.223558);
        assertLibrary(libraryMap.get("711618"), "711618", "KB국민은행과 함께하는 나무 작은도서관", "서울특별시 노원구 동일로 1405", 127.0602181, 37.6538195);
    }

    private Map<String, Library> toMap(List<Library> libraries) {
        return libraries.stream()
                .collect(Collectors.toMap(Library::getLibraryCode, library -> library));
    }

    private void assertLibrary(Library library, String libraryCode, String name, String address, double x, double y) {
        assertEquals(library.getName(), name);
        assertEquals(library.getAddress(), address);
        assertEquals(library.getLibraryCode(), libraryCode);
        assertEquals(library.getLocation().getX(), x);
        assertEquals(library.getLocation().getY(), y);
    }

    List<Library> findLibraries() {
        return entityManager.createQuery("""
                        SELECT l FROM
                        Library l
                        Where l.libraryCode IN :codes
                        """, Library.class)
                .setParameter("codes", Arrays.asList("127058", "711618", "148096"))
                .getResultList();
    }
}