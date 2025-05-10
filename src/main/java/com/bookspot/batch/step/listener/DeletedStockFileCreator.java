package com.bookspot.batch.step.listener;

import com.bookspot.batch.global.file.stock.StockFilenameUtil;
import com.bookspot.batch.step.writer.ExistsStockChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@RequiredArgsConstructor
public class DeletedStockFileCreator implements StepExecutionListener {
    private final ExistsStockChecker existsStockChecker;
    private final String deleteFilePath;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if(!stepExecution.getExitStatus().equals(ExitStatus.COMPLETED))
            return StepExecutionListener.super.afterStep(stepExecution);


        StringBuilder sb = new StringBuilder();
        existsStockChecker.processNonExistStock(
                (bookId, libraryId) -> {
                    sb.append("%d,%d\n".formatted(bookId, libraryId));
                }
        );

        if(sb.isEmpty())
            return StepExecutionListener.super.afterStep(stepExecution);

        try {
            Path path = Paths.get(deleteFilePath);
            Files.writeString(path, sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("DELETE STOCK CSV 저장 실패", e);
        }

        return StepExecutionListener.super.afterStep(stepExecution);
    }
}
