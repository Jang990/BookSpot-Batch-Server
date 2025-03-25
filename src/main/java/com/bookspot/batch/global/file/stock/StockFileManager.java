package com.bookspot.batch.global.file.stock;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class StockFileManager {
    public void deleteInnerFiles(String directoryPath) throws IOException {
        Path directory = Path.of(directoryPath);
        if(Files.notExists(directory))
            return;

        Files.list(directory)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        throw new RuntimeException("파일 삭제 실패", e);
                    }
                });
    }

    public List<StockFilenameElement> convertInnerFiles(String rootDirectoryPath) throws IOException {
        Path rootDirectory = Path.of(rootDirectoryPath);

        return Files.list(rootDirectory)
                .filter(Files::isRegularFile)
                .map(file -> StockFilenameUtil.parse(file.toFile().getName()))
                .toList();
    }
}
