package com.bookspot.batch.stock;

import com.bookspot.batch.stock.data.LibraryStock;
import com.bookspot.batch.stock.data.LibraryStockCsvData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class StockWriterConfig {
    private final DataSource dataSource;

    @Bean
    public JdbcBatchItemWriter<LibraryStock> libraryStockWriter() {
        JdbcBatchItemWriter<LibraryStock> writer = new JdbcBatchItemWriterBuilder<LibraryStock>()
                .dataSource(dataSource)
                .sql("""
                        INSERT IGNORE INTO library_stock
                        (book_id, library_id)
                        VALUES(?, ?);
                        """)
                .itemPreparedStatementSetter(
                        (book, ps) -> {
                            ps.setLong(1, book.getBookId());
                            ps.setLong(2, book.getLibraryId());
                        })
                .assertUpdates(false)
                .build();
        return writer;
    }
}
