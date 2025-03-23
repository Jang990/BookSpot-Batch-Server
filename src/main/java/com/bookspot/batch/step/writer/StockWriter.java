package com.bookspot.batch.step.writer;

import com.bookspot.batch.data.LibraryStock;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import javax.sql.DataSource;

public class StockWriter extends JdbcBatchItemWriter<LibraryStock> {
    public StockWriter(DataSource dataSource) {
        setDataSource(dataSource);
        setSql("""
                INSERT INTO library_stock(book_id, library_id, created_at, updated_at)
                VALUES(?, ?, NOW(), NOW())
                ON DUPLICATE KEY UPDATE updated_at = NOW();
                """);
        setItemPreparedStatementSetter(
                (book, ps) -> {
                    ps.setLong(1, book.getBookId());
                    ps.setLong(2, book.getLibraryId());
                });
        setAssertUpdates(false);
    }
}
