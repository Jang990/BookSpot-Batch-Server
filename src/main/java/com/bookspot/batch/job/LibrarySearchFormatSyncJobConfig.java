package com.bookspot.batch.job;

import com.bookspot.batch.data.file.csv.IsbnSearchFormatFileSpec;
import com.bookspot.batch.global.properties.files.BookSpotDirectoryProperties;
import com.bookspot.batch.job.listener.alert.AlertJobListener;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class LibrarySearchFormatSyncJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final BookSpotDirectoryProperties bookSpotDirectoryProperties;

    @Bean
    public Job librarySearchFormatSyncJob(AlertJobListener alertJobListener) {
        return new JobBuilder("librarySearchFormatSyncJob", jobRepository)
                .start(librarySearchPageSyncStep())
                .listener(alertJobListener)
                .build();
    }

    @Bean
    public Step librarySearchPageSyncStep() {
        String targetFile = bookSpotDirectoryProperties.librarySync() + "/libraryHomePages_Prod_Result.csv";
        return new StepBuilder("librarySearchFormatSyncStep", jobRepository)
                .<IsbnSearchFormatFileSpec, IsbnSearchFormatFileSpec>chunk(200, transactionManager)
                .reader(librarySearchFormatCsvReader(targetFile))
                .processor(librarySearchFormatCsvProcessor())
                .writer(librarySearchFormatCsvWriter(dataSource))
                .build();
    }

    @Bean
    public FlatFileItemReader<IsbnSearchFormatFileSpec> librarySearchFormatCsvReader(String targetFile) {
        return new FlatFileItemReaderBuilder<IsbnSearchFormatFileSpec>()
                .name("librarySearchCsvReader")
                .resource(new FileSystemResource(targetFile))
                .delimited()
                .delimiter(",")
                .names("id", "name", "isbn_search_prefix")
                .fieldSetMapper(fieldSet -> new IsbnSearchFormatFileSpec(
                        fieldSet.readLong("id"),
                        fieldSet.readString("name"),
                        fieldSet.readString("isbn_search_prefix")
                ))
                .linesToSkip(1)
                .build();
    }

    @Bean
    public ItemProcessor<IsbnSearchFormatFileSpec, IsbnSearchFormatFileSpec> librarySearchFormatCsvProcessor() {
        return item -> {
            if (item.id() == null
                    || item.name() == null || item.name().isBlank()
                    || item.format() == null || item.format().isBlank())
                return null;
            else
                return item;
        };
    }

    @Bean
    public JdbcBatchItemWriter<IsbnSearchFormatFileSpec> librarySearchFormatCsvWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<IsbnSearchFormatFileSpec>()
                .dataSource(dataSource)
                .sql("""
                        UPDATE library
                        SET isbn_search_pattern = :format
                        WHERE id = :id
                        """)
                .beanMapped()
                .build();
    }
}
