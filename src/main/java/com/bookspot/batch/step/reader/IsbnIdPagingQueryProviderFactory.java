package com.bookspot.batch.step.reader;

import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;

import javax.sql.DataSource;

public class IsbnIdPagingQueryProviderFactory extends SqlPagingQueryProviderFactoryBean {

    public IsbnIdPagingQueryProviderFactory(DataSource dataSource) {
        setDataSource(dataSource);
        setSelectClause("select id, isbn13");
        setFromClause("from book");
        setSortKey("id");
    }

}
