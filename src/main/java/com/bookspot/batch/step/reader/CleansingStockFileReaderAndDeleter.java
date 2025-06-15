package com.bookspot.batch.step.reader;

import com.bookspot.batch.global.FileService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.core.io.Resource;

public class CleansingStockFileReaderAndDeleter extends CleansingStockFileReader {
    private final Resource targetResource;
    private final StepExecution stepExecution;
    private final FileService fileService;

    public CleansingStockFileReaderAndDeleter(Resource sourceFile, StepExecution stepExecution, FileService fileService) {
        super(sourceFile);
        this.targetResource = sourceFile;
        this.stepExecution = stepExecution;
        this.fileService = fileService;
    }

    @Override
    public void close() throws ItemStreamException {
        super.close();

        if(!stepExecution.getExitStatus().equals(ExitStatus.COMPLETED))
            return;

        fileService.delete(targetResource);
    }
}
