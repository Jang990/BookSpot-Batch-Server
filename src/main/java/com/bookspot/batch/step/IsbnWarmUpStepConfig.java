package com.bookspot.batch.step;

import com.bookspot.batch.step.service.IsbnMemoryRepository;
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
public class IsbnWarmUpStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    private final IsbnMemoryRepository isbnEclipseMemoryRepository;

    @Bean
    public Step isbnMemoryClearStep() {
        return new StepBuilder("isbnMemoryClearStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    isbnEclipseMemoryRepository.clearMemory();
                    return RepeatStatus.FINISHED;
                },platformTransactionManager)
                .build();
    }

    @Bean
    public Step isbnWarmUpStep(JdbcPagingItemReader<String> isbnReader) {
        return new StepBuilder("isbnWarmUpStep", jobRepository)
                .<String, String>chunk(1000, platformTransactionManager)
                .reader(isbnReader)
                .writer(isbnWriter())
                    .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public JdbcPagingItemReader<String> isbnReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<String>()
                .name("isbnReader")
                .dataSource(dataSource)
                .queryProvider(isbnPagingQueryProvider())
                .pageSize(1000)
                .rowMapper((rs, rowNum) -> rs.getString("isbn13"))
                .build();
    }

    @Bean
    public PagingQueryProvider isbnPagingQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setSelectClause("select isbn13");
        factoryBean.setFromClause("from book");
        factoryBean.setSortKey("isbn13");
        return factoryBean.getObject();
    }

    @Bean
    public ItemWriter<String> isbnWriter() {
        ItemWriterAdapter<String> adapter = new ItemWriterAdapter<>();
        adapter.setTargetObject(isbnEclipseMemoryRepository);
        adapter.setTargetMethod("add");
        return adapter;
    }
}
