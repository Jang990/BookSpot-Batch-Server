package com.bookspot.batch.step;

import com.bookspot.batch.data.LibraryForFileParsing;
import com.bookspot.batch.data.crawler.StockFileData;
import com.bookspot.batch.step.processor.StockFilePathParser;
import com.bookspot.batch.step.writer.file.stock.StockFileDownloader;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;

@Configuration
@RequiredArgsConstructor
public class StockFileManagerStepConfig {
    private static final int STOCK_CSV_DOWNLOAD_CHUNK_SIZE = 10;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    private final StockFilePathParser stockFilePathParser;
    private final StockFileDownloader stockFileDownloader;

    @Bean
    public Step stockCsvDownloadStep() throws Exception {
        return new StepBuilder("stockCsvDownloadStep", jobRepository)
                .<LibraryForFileParsing, StockFileData>chunk(STOCK_CSV_DOWNLOAD_CHUNK_SIZE, platformTransactionManager)
                .reader(libraryForFileParsingReader())
                .processor(stockFilePathParser)
                .writer(stockFileDownloaderWriter())
                .build();
    }


    @Bean
    public Step stockCsvDeleteStep() {
        return new StepBuilder("stockCsvDeleteStep", jobRepository)
                .tasklet(stockCsvDeleteTasklet(null), platformTransactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet stockCsvDeleteTasklet(@Value("#{jobParameters['rootDirPath']}") String filePath) {
        return (contribution, chunkContext) -> {
            Files.delete(Paths.get(filePath)); // 파일 삭제
            return RepeatStatus.FINISHED;
        };
    }


    @Bean
    public JdbcPagingItemReader<LibraryForFileParsing> libraryForFileParsingReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<LibraryForFileParsing>()
                .name("libraryStockDataReader")
                .dataSource(dataSource)
                .queryProvider(libraryStockPagingQueryProvider())
                .pageSize(STOCK_CSV_DOWNLOAD_CHUNK_SIZE)
                .rowMapper((rs, rowNum) -> {
                    Timestamp stockUpdatedAt = rs.getTimestamp("stock_updated_at");
                    return new LibraryForFileParsing(
                            rs.getLong("id"),
                            rs.getString("library_code"),
                            rs.getString("naru_detail"),
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

    @Bean
    public ItemWriter<StockFileData> stockFileDownloaderWriter() {
        ItemWriterAdapter<StockFileData> adapter = new ItemWriterAdapter<>();
        adapter.setTargetObject(stockFileDownloader);
        adapter.setTargetMethod("download");
        return adapter;
    }

}
