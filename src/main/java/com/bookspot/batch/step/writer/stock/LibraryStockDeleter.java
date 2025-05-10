package com.bookspot.batch.step.writer.stock;

import com.bookspot.batch.data.LibraryStock;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import javax.sql.DataSource;

public class LibraryStockDeleter extends JdbcBatchItemWriter<LibraryStock> {
    public LibraryStockDeleter(DataSource dataSource) {
        setDataSource(dataSource);
        setSql("""
                DELETE FROM library_stock
                WHERE book_id = ? AND library_id = ?
                """);
        setItemPreparedStatementSetter((stock, ps) -> {
            ps.setLong(1, stock.getBookId());
            ps.setLong(2, stock.getLibraryId());
        });
    }
}
