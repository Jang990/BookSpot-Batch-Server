package com.bookspot.batch.step.listener;

import com.bookspot.batch.global.FileService;
import com.bookspot.batch.step.writer.ExistsStockChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@RequiredArgsConstructor
public class DeletedStockFileCreator implements StepExecutionListener {
    private final FileService fileService;
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

        fileService.saveNewFile(deleteFilePath, sb.toString());
        return StepExecutionListener.super.afterStep(stepExecution);
    }
}
