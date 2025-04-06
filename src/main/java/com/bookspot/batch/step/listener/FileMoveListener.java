package com.bookspot.batch.step.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;

public class FileMoveListener implements StepExecutionListener {
    private final Resource sourceFile;
    private final String outputDir;

    public FileMoveListener(Resource sourceFile, String outputDir) {
        Objects.requireNonNull(sourceFile);
        Objects.requireNonNull(outputDir);
        this.sourceFile = sourceFile;
        this.outputDir = outputDir;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        try {
            Path targetDir = Path.of(outputDir);
            Path targetPath = targetDir.resolve(sourceFile.getFilename());

            Files.move(sourceFile.getFile().toPath(), targetPath);
            return StepExecutionListener.super.afterStep(stepExecution);
        } catch (NoSuchFileException e) {
            throw new RuntimeException(sourceFile + " 경로의 파일을 찾을 수 없음", e);
        } catch (IOException e) {
            throw new RuntimeException("파일 이동 실패", e);
        }
    }
}
