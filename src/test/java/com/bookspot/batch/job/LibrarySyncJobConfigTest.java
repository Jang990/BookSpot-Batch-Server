package com.bookspot.batch.job;

import com.bookspot.batch.TestQueryUtil;
import com.bookspot.batch.data.Library;
import com.bookspot.batch.data.crawler.LibraryNaruDetail;
import com.bookspot.batch.global.crawler.naru.NaruRequestCreator;
import com.bookspot.batch.global.file.NaruFileDownloader;
import com.bookspot.batch.step.reader.LibraryNaruDetailReader;
import com.bookspot.batch.step.reader.file.excel.library.LibraryFileDownloader;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@BatchJobTest
class LibrarySyncJobConfigTest {

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    Job librarySyncJob;

    @Autowired
    EntityManager entityManager;

    @MockBean
    NaruRequestCreator requestCreator;

    @MockBean
    NaruFileDownloader naruFileDownloader;

    @MockBean
    LibraryNaruDetailReader libraryNaruDetailReader;

    private static String SAMPLE_FILE_PATH_STRING = "src/test/resources/files/sample/librarySync/library_list.xlsx";
    private static String DEST_FILE_PATH_STRING = "src/test/resources/files/librarySync/library_list.xlsx";

    @BeforeEach
    void setUp() throws Exception {
        changeTestingFile();

        when(libraryNaruDetailReader.read()).thenReturn(
                new LibraryNaruDetail("2.28도서관", "대구광역시 중구 2·28길 9", "29981"),
                new LibraryNaruDetail("KB국민은행과 함께하는 나무 작은도서관", "서울특별시 노원구 동일로 1405", "25937"),
                new LibraryNaruDetail("U보라작은도서관",	"경상남도 김해시 전하로176번길 71, 반도보라아파트 주민공동시설 2층", "8504"),
                null
        );

        jobLauncherTestUtils.setJob(librarySyncJob);
    }

    private void changeTestingFile() throws IOException {
        final Path SAMPLE_FILE = Path.of(SAMPLE_FILE_PATH_STRING);
        final Path DEST_FILE = Path.of(DEST_FILE_PATH_STRING); // 복사 대상
        if(!Files.exists(SAMPLE_FILE))
            fail("테스트할 샘플 도서관 정보 엑셀 파일이 존재하지 않음");

        // 테스트 최적화를 위해 실제 파일 다운로드 대신 파일 복사
        doAnswer((invocationOnMock -> {
            Files.copy(SAMPLE_FILE, DEST_FILE, StandardCopyOption.REPLACE_EXISTING);
            return invocationOnMock;
        })).when(naruFileDownloader).downloadSync(anyString(), any(), anyString());
    }

    // TODO: org.springframework.dao.EmptyResultDataAccessException: Item 0 of 3 did not update any rows: [LibraryNaruDetail[name=2.28도서관, address=대구광역시 중구 2·28길 9, naruDetail=29981]] 스킵해도 되는가?
    @Test
    void 정상처리() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(new JobParametersBuilder()
                .addString(LibrarySyncJobConfig.LIBRARY_FILE_PARAM_NAME, "src/test/resources/files/librarySync/library_list.xlsx")
                .addLocalDateTime("temp_date", LocalDateTime.now())
                .toJobParameters());

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        Map<String, Library> libraryMap = toMap(TestQueryUtil.findLibraries(entityManager, List.of("127058", "711618", "148096"), 3));

        assertLibrary(libraryMap.get("127058"), "127058", "2.28도서관", "대구광역시 중구 2·28길 9", 128.5894055, 35.8592504);
        assertLibrary(libraryMap.get("148096"), "148096", "U보라작은도서관", "경상남도 김해시 전하로176번길 71, 반도보라아파트 주민공동시설 2층", 128.874226, 35.223558);
        assertLibrary(libraryMap.get("711618"), "711618", "KB국민은행과 함께하는 나무 작은도서관", "서울특별시 노원구 동일로 1405", 127.0602181, 37.6538195);

        assertFalse(Files.exists(Path.of(DEST_FILE_PATH_STRING)));
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
}