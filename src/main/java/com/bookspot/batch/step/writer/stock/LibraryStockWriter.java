package com.bookspot.batch.step.writer.stock;

import com.bookspot.batch.data.LibraryStock;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import javax.sql.DataSource;

public class LibraryStockWriter extends JdbcBatchItemWriter<LibraryStock> {
    public LibraryStockWriter(DataSource dataSource) {
        setDataSource(dataSource);
        setSql("""
                INSERT INTO library_stock
                (library_id, book_id, created_at)
                VALUES (?, ?, NOW(6));
                """);
        setItemPreparedStatementSetter((stock, ps) -> {
            ps.setLong(1, stock.getLibraryId());
            ps.setLong(2, stock.getBookId());
        });
    }
}
