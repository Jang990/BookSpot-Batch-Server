package com.bookspot.batch.global;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;

@Service
public class FileService {
    public void delete(Resource deleteTarget) {
        try {
            Files.delete(deleteTarget.getFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
