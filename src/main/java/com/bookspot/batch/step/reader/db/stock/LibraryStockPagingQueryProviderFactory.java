package com.bookspot.batch.step.reader.db.stock;

import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

public class LibraryStockPagingQueryProviderFactory extends SqlPagingQueryProviderFactoryBean {

    public LibraryStockPagingQueryProviderFactory(DataSource dataSource) {
        setDataSource(dataSource);
        setSelectClause("select id, book_id, library_id");
        setFromClause("from library_stock");
        setWhereClause("where library_id = :libraryId");
        setSortKey("id");
    }

}
