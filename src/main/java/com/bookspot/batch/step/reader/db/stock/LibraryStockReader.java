package com.bookspot.batch.step.reader.db.stock;

import com.bookspot.batch.data.LibraryStockDto;
import org.springframework.batch.item.database.JdbcPagingItemReader;

import javax.sql.DataSource;
import java.util.Map;

public class LibraryStockReader  extends JdbcPagingItemReader<LibraryStockDto> {
    public LibraryStockReader(
            DataSource dataSource,
            LibraryStockPagingQueryProviderFactory libraryStockPagingQueryProviderFactory,
            final long libraryId,
            final int pageSize) throws Exception {
        setName("libraryStockReader");
        setDataSource(dataSource);
        setQueryProvider(libraryStockPagingQueryProviderFactory.getObject());
        setPageSize(pageSize);
        setRowMapper(
                (rs, rowNum) -> new LibraryStockDto(
                        rs.getLong("library_id"),
                        rs.getLong("book_id")
                )
        );
        setParameterValues(Map.of("libraryId", libraryId));
    }
}
