package com.bookspot.batch.step.writer.file.stock;


import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.global.file.spec.NormalizedStockCsvSpec;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.core.io.FileSystemResource;

public class StockNormalizeFileWriter extends FlatFileItemWriter<LibraryStock> {

    public StockNormalizeFileWriter(String outputFile) {
        setResource(new FileSystemResource(outputFile));
        setLineAggregator(
                item -> NormalizedStockCsvSpec.createLine(
                        item.getBookId(),
                        item.getLibraryId()
                )
        );
    }
}
