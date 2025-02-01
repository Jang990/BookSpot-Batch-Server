package com.bookspot.batch.step;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class MissingStockDeleteStepConfig {
    private final static int CHUNK_SIZE = 10_000;

    private final DataSource dataSource;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step missingStockDeleteStep() throws Exception {
        return new StepBuilder("missingStockDeleteStep", jobRepository)
                .<Long, Long>chunk(CHUNK_SIZE, transactionManager)
                .reader(nonUpdatedStockIdReader(null))
                .writer(missingStockDeleter())
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<Long> nonUpdatedStockIdReader(
            @Value("#{jobParameters['libraryId']}") Long libraryId) throws Exception {
        return new JdbcPagingItemReaderBuilder<Long>()
                .name("nonUpdatedStockIdReader")
                .dataSource(dataSource)
                .queryProvider(nonUpdatedStockIdPagingQueryProvider())
                .pageSize(CHUNK_SIZE)
                .parameterValues(Collections.singletonMap("libraryId", libraryId))
                .rowMapper((rs, rowNum) -> rs.getLong("id"))
                .build();
    }

    @Bean
    public PagingQueryProvider nonUpdatedStockIdPagingQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setSelectClause("SELECT id");
        factoryBean.setFromClause("FROM library_stock");
        factoryBean.setWhereClause("WHERE library_id = :libraryId AND MONTH(updated_at) != MONTH(CURRENT_DATE())");
        factoryBean.setSortKey("id");
        return factoryBean.getObject();
    }

    @Bean
    public JdbcBatchItemWriter<Long> missingStockDeleter() {
        JdbcBatchItemWriter<Long> writer = new JdbcBatchItemWriterBuilder<Long>()
                .dataSource(dataSource)
                .sql("""
                        DELETE FROM library_stock
                        WHERE id = ?
                        """)
                .itemPreparedStatementSetter(
                        (stockId, ps) -> {
                            ps.setLong(1, stockId);
                        })
                .build();
        return writer;
    }

}
