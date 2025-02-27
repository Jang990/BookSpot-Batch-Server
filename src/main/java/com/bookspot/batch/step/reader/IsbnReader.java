package com.bookspot.batch.step.reader;

import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;

import javax.sql.DataSource;

public class IsbnReader extends JdbcPagingItemReader<String> {

    public IsbnReader(
            DataSource dataSource,
            PagingQueryProvider isbnIdPagingQueryProvider,
            final int pageSize) {
        setName("isbnReader");
        setDataSource(dataSource);
        setQueryProvider(isbnIdPagingQueryProvider);
        setPageSize(pageSize);
        setRowMapper((rs, rowNum) -> rs.getString("isbn13"));
    }
}
