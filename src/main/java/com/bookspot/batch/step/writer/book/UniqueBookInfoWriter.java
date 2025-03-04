package com.bookspot.batch.step.writer.book;

import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import javax.sql.DataSource;
import java.sql.Types;

public class UniqueBookInfoWriter extends JdbcBatchItemWriter<ConvertedUniqueBook> {
    public UniqueBookInfoWriter(DataSource dataSource) {
        setDataSource(dataSource);
        setSql("""
                INSERT IGNORE INTO book
                (isbn13, title, subject_code, volume_name, author, publisher)
                VALUES (?, ?, ?, ?, ?, ?);
                """);
        setItemPreparedStatementSetter((book, ps) -> {
            ps.setString(1, book.getIsbn13());
            ps.setString(2, book.getTitle());
            ps.setObject(3, book.getSubjectCode(), Types.INTEGER);
            ps.setString(4, book.getVolume());
            ps.setString(5, book.getAuthor());
            ps.setString(6, book.getPublisher());
        });

    }
}
