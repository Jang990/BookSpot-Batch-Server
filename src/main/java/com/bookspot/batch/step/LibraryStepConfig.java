package com.bookspot.batch.step;

import com.bookspot.batch.data.Library;
import com.bookspot.batch.global.FileService;
import com.bookspot.batch.job.LibrarySyncJobConfig;
import com.bookspot.batch.step.listener.LibrarySyncStepListener;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.listener.alert.AlertStepListener;
import com.bookspot.batch.step.reader.LibraryExcelFileReaderAndDeleter;
import com.bookspot.batch.step.reader.file.excel.library.LibraryExcelRowMapper;
import com.bookspot.batch.step.reader.file.excel.library.LibraryFileDownloader;
import com.bookspot.batch.step.writer.LibraryWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
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
            LibraryFileDownloader libraryFileDownloader,
            LibraryExcelFileReaderAndDeleter libraryExcelFileReaderAndDeleter,
            LibraryWriter libraryWriter,
            AlertStepListener alertStepListener
    ) {
        return new StepBuilder("librarySyncStep", jobRepository)
                .<Library, Library>chunk(LibraryStepConst.LIBRARY_CHUNK_SIZE, platformTransactionManager)
                .reader(libraryExcelFileReaderAndDeleter)
                .writer(libraryWriter)
                .listener(new LibrarySyncStepListener(libraryFileDownloader))
                .listener(stepLoggingListener)
                .listener(alertStepListener)
                .build();
    }

    @Bean
    @StepScope
    public LibraryExcelFileReaderAndDeleter libraryExcelFileReaderAndDeleter(
            @Value("#{stepExecution}") StepExecution stepExecution,
            FileService fileService,
            LibraryExcelRowMapper libraryExcelRowMapper,
            @Value(LibrarySyncJobConfig.LIBRARY_FILE_PARAM) String filePath
    ) {
        return new LibraryExcelFileReaderAndDeleter(stepExecution, fileService, libraryExcelRowMapper, filePath);
    }

    @Bean
    public LibraryWriter libraryWriter(DataSource dataSource) {
        return new LibraryWriter(dataSource);
    }

}
