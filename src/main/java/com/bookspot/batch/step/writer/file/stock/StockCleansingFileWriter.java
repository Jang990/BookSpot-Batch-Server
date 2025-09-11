package com.bookspot.batch.step.writer.file.stock;


import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.global.file.spec.CleansingStockCsvSpec;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.core.io.FileSystemResource;

public class StockCleansingFileWriter extends FlatFileItemWriter<LibraryStock> {

    public StockCleansingFileWriter(String outputFile) {
        setResource(new FileSystemResource(outputFile));
        setLineAggregator(
                item -> CleansingStockCsvSpec.createLine(
                        item.getBookId(),
                        item.getLibraryId(),
                        item.getSubjectCode()
                )
        );
    }
}
