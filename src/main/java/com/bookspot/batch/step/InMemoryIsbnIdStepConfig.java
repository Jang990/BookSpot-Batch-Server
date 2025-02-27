package com.bookspot.batch.step;

import com.bookspot.batch.step.service.memory.bookid.Isbn13MemoryData;
import com.bookspot.batch.step.service.memory.bookid.IsbnMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class InMemoryIsbnIdStepConfig {
    private static final int WARM_UP_CHUNK_SIZE = 10_000;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    private final IsbnMemoryRepository isbnEclipseMemoryRepository;

    @Bean
    public Step inMemoryIsbnIdClearStep() {
        return new StepBuilder("inMemoryIsbnIdClearStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    isbnEclipseMemoryRepository.clearMemory();
                    return RepeatStatus.FINISHED;
                },platformTransactionManager)
                .build();
    }

    @Bean
    public Step inMemoryIsbnIdWarmUpStep() throws Exception {
        return new StepBuilder("inMemoryIsbnIdWarmUpStep", jobRepository)
                .<Isbn13MemoryData, Isbn13MemoryData>chunk(WARM_UP_CHUNK_SIZE, platformTransactionManager)
                .reader(isbnIdReader())
                .writer(inMemoryIsbnIdWriter())
                    .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Isbn13MemoryData> isbnIdReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<Isbn13MemoryData>()
                .name("isbnIdReader")
                .dataSource(dataSource)
                .queryProvider(isbnIdPagingQueryProvider())
                .pageSize(WARM_UP_CHUNK_SIZE)
                .rowMapper((rs, rowNum) -> new Isbn13MemoryData(rs.getString("isbn13"), rs.getLong("id")))
                .build();
    }

    @Bean
    public PagingQueryProvider isbnIdPagingQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setSelectClause("select id, isbn13");
        factoryBean.setFromClause("from book");
        factoryBean.setSortKey("id");
        return factoryBean.getObject();
    }

    @Bean
    public ItemWriter<Isbn13MemoryData> inMemoryIsbnIdWriter() {
        ItemWriterAdapter<Isbn13MemoryData> adapter = new ItemWriterAdapter<>();
        adapter.setTargetObject(isbnEclipseMemoryRepository);
        adapter.setTargetMethod("add");
        return adapter;
    }
}
