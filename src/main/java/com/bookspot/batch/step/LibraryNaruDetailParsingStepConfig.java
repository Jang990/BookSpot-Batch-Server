package com.bookspot.batch.step;

import com.bookspot.batch.data.crawler.LibraryNaruDetail;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.listener.alert.AlertStepListener;
import com.bookspot.batch.step.reader.LibraryNaruDetailReader;
import com.bookspot.batch.step.writer.LibraryNaruDetailWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class LibraryNaruDetailParsingStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final StepLoggingListener stepLoggingListener;

    @Bean
    public Step libraryNaruDetailParsingStep(
            LibraryNaruDetailReader libraryNaruDetailReader,
            LibraryNaruDetailWriter libraryNaruDetailWriter,
            AlertStepListener alertStepListener
    ) {
        return new StepBuilder("libraryNaruDetailParsingStep", jobRepository)
                .<LibraryNaruDetail, LibraryNaruDetail>chunk(LibraryStepConst.LIBRARY_CHUNK_SIZE, platformTransactionManager)
                .reader(libraryNaruDetailReader)
                .writer(libraryNaruDetailWriter)
                .listener(stepLoggingListener)
                .listener(alertStepListener)
                .build();
    }

    @Bean
    public LibraryNaruDetailWriter libraryNaruDetailWriter(DataSource dataSource) {
        return new LibraryNaruDetailWriter(dataSource);
    }
}
