package com.bookspot.batch.job.temp.library.homepage;

import com.bookspot.batch.global.properties.files.BookSpotDirectoryProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HomePageCsvFileCreator {
    /*
    library_id, name, home_page (home_page 정렬) 뽑아내기 -> csv 파일 만들어내기
     */
    private final JdbcTemplate jdbcTemplate;
    private final BookSpotDirectoryProperties dirProperties;

    public void run(String filePath) {
        // 데이터 조회
        List<Map<String, Object>> libraries = jdbcTemplate.queryForList(
                "SELECT id, name, home_page FROM library ORDER BY home_page"
        );

        // CSV 파일 생성
        Path outputPath = Paths.get(filePath);
        try (OutputStreamWriter osw = new OutputStreamWriter(
                new FileOutputStream(outputPath.toFile()),
                StandardCharsets.UTF_8
        );
             BufferedWriter writer = new BufferedWriter(osw)) {

            writer.write('\uFEFF'); // BOM 추가

            // 헤더 작성
            writer.write("id,name,home_page,isbn_search_prefix");
            writer.newLine();

            // 데이터 작성
            for (Map<String, Object> lib : libraries) {
                writer.write(
                        lib.get("id") + "," +
                                lib.get("name") + "," +
                                lib.get("home_page") + ","  // isbn_search_prefix는 비워둠
                );
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IllegalStateException("CSV 파일 생성 실패", e);
        }
    }

//    @GetMapping("/temp/run")
    public ResponseEntity<String> runTemp() {
        run(dirProperties.librarySync() + "/libraryHomePages.csv");
        return ResponseEntity.ok("진행!");
    }

}
