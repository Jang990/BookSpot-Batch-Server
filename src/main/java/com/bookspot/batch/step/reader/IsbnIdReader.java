package com.bookspot.batch.step.reader;

import com.bookspot.batch.step.service.memory.bookid.Isbn13MemoryData;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;

import javax.sql.DataSource;

public class IsbnIdReader extends JdbcPagingItemReader<Isbn13MemoryData> {

    public IsbnIdReader(
            DataSource dataSource,
            PagingQueryProvider isbnIdPagingQueryProvider,
            final int pageSize) {
        setName("isbnIdReader");
        setDataSource(dataSource);
        setQueryProvider(isbnIdPagingQueryProvider);
        setPageSize(pageSize);
        setRowMapper((rs, rowNum) -> new Isbn13MemoryData(rs.getString("isbn13"), rs.getLong("id")));
    }
}
