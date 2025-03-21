package com.bookspot.batch.step;

import com.bookspot.batch.data.crawler.LibraryNaruDetail;
import com.bookspot.batch.step.reader.LibraryNaruDetailReader;
import com.bookspot.batch.step.writer.LibraryNaruDetailWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class LibraryNaruDetailParsingStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Step libraryNaruDetailParsingStep(
            LibraryNaruDetailReader libraryNaruDetailReader,
            LibraryNaruDetailWriter libraryNaruDetailWriter) {
        return new StepBuilder("libraryNaruDetailParsingStep", jobRepository)
                .<LibraryNaruDetail, LibraryNaruDetail>chunk(LibraryStepConst.LIBRARY_CHUNK_SIZE, platformTransactionManager)
                .reader(libraryNaruDetailReader)
                .writer(libraryNaruDetailWriter)
                .build();
    }

    @Bean
    public LibraryNaruDetailWriter libraryNaruDetailWriter(DataSource dataSource) {
        return new LibraryNaruDetailWriter(dataSource);
    }
}
