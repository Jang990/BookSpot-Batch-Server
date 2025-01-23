package com.bookspot.batch.step.writer;

import com.bookspot.batch.book.data.Book;
import com.bookspot.batch.stock.data.LibraryStockCsvData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class BookWriterConfig {
    private final DataSource dataSource;

    // 도서관 재고 csv -> book 테이블 저장
    @Bean
    public JdbcBatchItemWriter<LibraryStockCsvData> stockBookWriter() {
        JdbcBatchItemWriter<LibraryStockCsvData> writer = new JdbcBatchItemWriterBuilder<LibraryStockCsvData>()
                .dataSource(dataSource)
                .sql("""
                        INSERT IGNORE INTO book
                        (isbn13, title, classification, volume_name)
                        VALUES(?, ?, ?, ?);
                        """)
                .itemPreparedStatementSetter(
                        (book, ps) -> {
                            ps.setString(1, book.getIsbn());
                            ps.setString(2, book.getTitle());
                            ps.setString(3, book.getSubjectCode());
                            ps.setString(4, book.getVolume());
                        })
                .assertUpdates(false)
                .build();
        return writer;
    }

    // 도서 csv -> book 테이블 저장
    @Bean
    public JdbcBatchItemWriter<Book> bookWriter() {
        JdbcBatchItemWriter<Book> writer = new JdbcBatchItemWriterBuilder<Book>()
                .dataSource(dataSource)
                .sql("""
                        INSERT IGNORE INTO book
                        (isbn13, title, classification, volume_name)
                        VALUES(?, ?, ?, ?);
                        """)
                .itemPreparedStatementSetter(
                        (book, ps) -> {
                            ps.setString(1, book.getIsbn13());
                            ps.setString(2, book.getTitle());
                            ps.setString(3, book.getSubjectCode());
                            ps.setString(4, book.getVolumeName());
                        })
                .assertUpdates(false) // 업데이트 되지 않는 row가 있어도 진행
                .build();
        return writer;
    }
}
