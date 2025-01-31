package com.bookspot.batch.step.reader;

import com.bookspot.batch.step.StockStepConst;
import com.bookspot.batch.data.LibraryForFileParsing;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Timestamp;

@Configuration
@RequiredArgsConstructor
public class LibraryItemReaderConfig {
    private final DataSource dataSource;

}
