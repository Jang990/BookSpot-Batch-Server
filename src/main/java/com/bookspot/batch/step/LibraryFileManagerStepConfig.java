package com.bookspot.batch.step;

import com.bookspot.batch.step.reader.file.excel.library.LibraryFileDownloader;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class LibraryFileManagerStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final LibraryFileDownloader downloader;

    @Bean
    public Step libraryExcelDownloadStep() {
        return new StepBuilder("libraryExcelDownloadStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    downloader.download();
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step libraryExcelDeleteStep() {
        return new StepBuilder("libraryExcelDeleteStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    downloader.delete();
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }
}
