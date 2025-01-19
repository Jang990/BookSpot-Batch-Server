package com.bookspot.batch.global.file;

import com.bookspot.batch.global.crawler.naru.NaruCommonRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Slf4j
@Service
public class NaruFileDownloader {

    private final WebClient webClient;

    public NaruFileDownloader() {
        this.webClient = WebClient.builder().build();
    }

    public void downloadSync(String url, NaruCommonRequest request, FileMetaData fileMetadata) {
        try {
            // 저장 경로 설정
            Path savePath = Path.of(fileMetadata.directory(), fileMetadata.fullName());

            // 폴더가 존재하지 않으면 생성
            Files.createDirectories(savePath.getParent());

            // 파일 저장 처리
            try (FileChannel channel = FileChannel.open(savePath,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {

                webClient.post()
                        .uri(url) // DTO에서 URL 가져오기
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .cookie("JSESSIONID", request.getJSessionId())
                        .body(BodyInserters.fromFormData("_csrf", request.getCsrfToken()))
                        .retrieve()
                        .bodyToFlux(DataBuffer.class) // 파일 데이터를 Flux로 받음
                        .toStream() // Flux를 Stream으로 변환 (동기 처리)
                        .forEach(dataBuffer -> {
                            try {
                                channel.write(dataBuffer.asByteBuffer());
                            } catch (IOException e) {
                                throw new RuntimeException("File write error", e);
                            }
                        });

                log.info("File downloaded successfully: {}", savePath);
            }
        } catch (WebClientResponseException e) {
            log.error("HTTP Error: {}", e.getStatusCode());
            log.error("Response Body: {}", e.getResponseBodyAsString());
        } catch (IOException e) {
            throw new RuntimeException("File save error", e);
        }
    }
}