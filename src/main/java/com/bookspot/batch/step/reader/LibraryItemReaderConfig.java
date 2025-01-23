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

    @Bean
    public JdbcPagingItemReader<LibraryForFileParsing> libraryForFileParsingReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<LibraryForFileParsing>()
                .name("libraryStockDataReader")
                .dataSource(dataSource)
                .queryProvider(libraryStockPagingQueryProvider())
                .pageSize(StockStepConst.DOWNLOAD_CHUNK_SIZE)
                .rowMapper((rs, rowNum) -> {
                    String libraryCode = rs.getString("library_code");
                    String naruDetail = rs.getString("naru_detail");
                    Timestamp stockUpdatedAt = rs.getTimestamp("stock_updated_at");
                    return new LibraryForFileParsing(
                            libraryCode,
                            naruDetail,
                            stockUpdatedAt == null ? null : stockUpdatedAt.toLocalDateTime().toLocalDate());
                })
                .build();
    }

    @Bean
    public PagingQueryProvider libraryStockPagingQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setSelectClause("select id, library_code, naru_detail, stock_updated_at");
        factoryBean.setFromClause("from Library");
        factoryBean.setWhereClause("where naru_detail is not null");
        factoryBean.setSortKey("id");
        return factoryBean.getObject();
    }
}
