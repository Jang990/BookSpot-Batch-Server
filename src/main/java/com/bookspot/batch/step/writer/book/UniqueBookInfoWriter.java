package com.bookspot.batch.step.writer.book;

import com.bookspot.batch.data.file.csv.StockCsvData;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import javax.sql.DataSource;

public class UniqueBookInfoWriter extends JdbcBatchItemWriter<StockCsvData> {
    public UniqueBookInfoWriter(DataSource dataSource) {
        setDataSource(dataSource);
        setSql("""
                INSERT IGNORE INTO book
                (isbn13, title, classification, volume_name, author, publisher)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """);
        setItemPreparedStatementSetter((book, ps) -> {
            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setInt(3, Integer.parseInt(book.getSubjectCode())); // TODO: 변경 예정
            ps.setString(4, book.getVolume());
            ps.setString(5, book.getAuthor());
            ps.setString(6, book.getPublisher());
        });

    }
}
