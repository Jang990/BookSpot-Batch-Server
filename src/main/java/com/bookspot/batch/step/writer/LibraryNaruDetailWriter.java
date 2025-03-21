package com.bookspot.batch.step.writer;

import com.bookspot.batch.data.crawler.LibraryNaruDetail;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import javax.sql.DataSource;

public class LibraryNaruDetailWriter extends JdbcBatchItemWriter<LibraryNaruDetail> {

    public LibraryNaruDetailWriter(DataSource dataSource) {
        setDataSource(dataSource);
        setSql("""
                UPDATE library
                SET naru_detail = ?
                WHERE address = ? AND name = ?
                """);
        setItemPreparedStatementSetter(
                (naruDetail, ps) -> {
                    ps.setString(1, naruDetail.naruDetail());
                    ps.setString(2, naruDetail.address());
                    ps.setString(3, naruDetail.name());
                });
    }

}
