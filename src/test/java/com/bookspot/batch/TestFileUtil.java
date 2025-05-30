package com.bookspot.batch;

import java.io.IOException;
import java.nio.file.*;

public class TestFileUtil {
    public static void copyAll(String from, String to) throws IOException {
        Path sourceDir = Paths.get(from);
        Path targetDir = Paths.get(to);

        // 대상 디렉토리가 없으면 생성
        if (Files.notExists(targetDir))
            Files.createDirectories(targetDir);

        // sourceDir에서 파일만 복사
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir)) {
            for (Path entry : stream) {
                if (!Files.isRegularFile(entry))  // 파일만 복사
                    continue;

                Path targetFile = targetDir.resolve(sourceDir.relativize(entry));

                // 대상 디렉토리가 없으면 생성
                if (Files.notExists(targetFile.getParent())) {
                    Files.createDirectories(targetFile.getParent());
                }

                // 파일 복사
                Files.copy(entry, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }

    }

    public static void moveAll(String from, String to) throws IOException {
        Path sourceDir = Paths.get(from);
        Path targetDir = Paths.get(to);

        // 대상 디렉토리가 없으면 생성
        if (Files.notExists(targetDir))
            Files.createDirectories(targetDir);

        // sourceDir에서 파일만 복사
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir)) {
            for (Path entry : stream) {
                if (!Files.isRegularFile(entry))  // 파일만 복사
                    continue;

                Path targetFile = targetDir.resolve(sourceDir.relativize(entry));

                // 대상 디렉토리가 없으면 생성
                if (Files.notExists(targetFile.getParent())) {
                    Files.createDirectories(targetFile.getParent());
                }

                // 파일 복사
                Files.move(entry, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }

    }

    public static void copy(String from, String to) throws IOException {
        Path sourceDir = Paths.get(from);
        Path targetDir = Paths.get(to);

        Files.copy(sourceDir, targetDir, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void delete(String sourceFilePath) throws IOException {
        Files.delete(Path.of(sourceFilePath));
    }

    public static void deleteAll(String targetDir) throws IOException {
        Path path = Path.of(targetDir);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                if (!Files.isRegularFile(entry))  // 파일만 복사
                    continue;

                // 파일 복사
                Files.delete(entry);
            }
        }
    }
}
