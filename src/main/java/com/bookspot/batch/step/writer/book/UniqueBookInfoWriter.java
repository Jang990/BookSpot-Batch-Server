package com.bookspot.batch.step.writer.book;

import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import javax.sql.DataSource;
import java.sql.Types;

public class UniqueBookInfoWriter extends JdbcBatchItemWriter<ConvertedUniqueBook> {
    public UniqueBookInfoWriter(DataSource dataSource) {
        setDataSource(dataSource);
        setSql("""
                INSERT IGNORE INTO book
                (isbn13, title, subject_code, author, publisher, publication_year, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, NOW(6), NOW(6));
                """);
        setItemPreparedStatementSetter((book, ps) -> {
            ps.setString(1, book.getIsbn13());
            ps.setString(2, book.getTitle());
            ps.setObject(3, book.getSubjectCode(), Types.INTEGER);
            ps.setString(4, book.getAuthor());
            ps.setString(5, book.getPublisher());
            if(book.getPublicationYear() == null)
                ps.setNull(6, Types.INTEGER);
            else
                ps.setInt(6, book.getPublicationYear().getValue());
        });
        setAssertUpdates(false);
    }

    @Override
    public void write(Chunk<? extends ConvertedUniqueBook> chunk) throws Exception {
        if (chunk.isEmpty())
            return;
        super.write(chunk);
    }
}
