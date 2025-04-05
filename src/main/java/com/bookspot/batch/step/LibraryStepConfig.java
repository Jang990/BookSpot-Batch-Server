package com.bookspot.batch.step;

import com.bookspot.batch.data.Library;
import com.bookspot.batch.job.LibrarySyncJobConfig;
import com.bookspot.batch.job.validator.FilePathJobParameterValidator;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.reader.LibraryExcelFileReader;
import com.bookspot.batch.step.reader.file.excel.library.LibraryExcelRowMapper;
import com.bookspot.batch.step.writer.LibraryWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class LibraryStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final StepLoggingListener stepLoggingListener;

    @Bean
    public Step librarySyncStep(
            LibraryExcelFileReader libraryExcelFileReader,
            LibraryWriter libraryWriter) {
        return new StepBuilder("librarySyncStep", jobRepository)
                .<Library, Library>chunk(LibraryStepConst.LIBRARY_CHUNK_SIZE, platformTransactionManager)
                .reader(libraryExcelFileReader)
                .writer(libraryWriter)
                .listener(stepLoggingListener)
                .build();
    }

    @Bean
    @StepScope
    public LibraryExcelFileReader libraryExcelFileReader(
            LibraryExcelRowMapper libraryExcelRowMapper,
            @Value(LibrarySyncJobConfig.LIBRARY_DIR_PARAM) String filePath) {
        return new LibraryExcelFileReader(libraryExcelRowMapper, filePath);
    }

    @Bean
    public LibraryWriter libraryWriter(DataSource dataSource) {
        return new LibraryWriter(dataSource);
    }

}
