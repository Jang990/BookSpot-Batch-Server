package com.bookspot.batch.step.listener;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileMoveListenerTest {
    FileMoveListener listener;

    static String sourceFileStr = "src/test/resources/files/move/source/test.txt";
    static String outputDir = "src/test/resources/files/move/after";

    static Resource sourceFile = new FileSystemResource(sourceFileStr);
    static Path sourceFilePath = Path.of(sourceFileStr);
    static Path afterFilePath = Path.of(outputDir.concat("/test.txt"));

    @Test
    void 파일_이동_성공() throws IOException {
        listener = new FileMoveListener(sourceFile, outputDir);
        listener.afterStep(null);

        assertFalse(Files.exists(sourceFilePath));
        assertTrue(Files.exists(afterFilePath));

        // 초기화
        Files.move(afterFilePath, sourceFilePath);
    }
}