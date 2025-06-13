package com.bookspot.batch.global;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class FileService {
    public void delete(Resource deleteTarget) {
        try {
            Files.delete(deleteTarget.getFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveNewFile(String filePath, String content) {
        try {
            Files.writeString(
                    Paths.get(filePath), content,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("DELETE STOCK CSV 저장 실패", e);
        }
    }
}
