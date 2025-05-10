package com.bookspot.batch.step.reader.db.stock;

import com.bookspot.batch.data.LibraryStockDto;
import com.bookspot.batch.global.file.stock.StockFilenameUtil;
import com.bookspot.batch.step.InsertStockStepConfig;
import com.bookspot.batch.step.partition.StockCsvPartitionConfig;
import lombok.RequiredArgsConstructor;
import org.eclipse.collections.impl.set.mutable.primitive.LongHashSet;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class LibraryStockReaderConfig {
    private final DataSource dataSource;

    @Bean
    @StepScope
    public LongHashSet libraryBookIdSet(LibraryStockReader libraryStockReader) throws Exception {
        LongHashSet bookIdSet = new LongHashSet();

        LibraryStockDto stock;
        while ((stock = libraryStockReader.read()) != null) {
            bookIdSet.add(stock.bookId());
        }

        return bookIdSet;
    }

    @Bean
    @StepScope
    public LibraryStockReader libraryStockReader(
            @Value(StockCsvPartitionConfig.STEP_EXECUTION_FILE) Resource file) throws Exception {
        return new LibraryStockReader(
                dataSource,
                libraryStockPagingQueryProviderFactory(),
                StockFilenameUtil.parse(file.getFilename()).libraryId(),
                InsertStockStepConfig.CHUNK_SIZE
        );
    }

    @Bean
    public LibraryStockPagingQueryProviderFactory libraryStockPagingQueryProviderFactory() {
        return new LibraryStockPagingQueryProviderFactory(dataSource);
    }
}
