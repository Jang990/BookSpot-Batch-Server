package com.bookspot.batch.job;

import com.bookspot.batch.global.crawler.naru.NaruRequestCreator;
import com.bookspot.batch.global.file.NaruFileDownloader;
import com.bookspot.batch.job.listener.LibrarySyncJobListener;
import com.bookspot.batch.job.validator.file.CustomFilePathValidators;
import com.bookspot.batch.job.validator.temp_FilePathJobParameterValidator;
import com.bookspot.batch.step.reader.file.excel.library.LibraryFileDownloader;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LibrarySyncJobConfig {
    private final JobRepository jobRepository;
    private final CustomFilePathValidators customFilePathValidators;
    public static final String LIBRARY_FILE_PARAM_NAME = "libraryFilePath";
    public static final String LIBRARY_FILE_PARAM = "#{jobParameters['libraryFilePath']}";

    @Bean
    public Job librarySyncJob(
            LibraryFileDownloader libraryFileDownloader,
            Step librarySyncStep,
            Step libraryNaruDetailParsingStep) {
        return new JobBuilder("librarySyncJob", jobRepository)
                // 도서관 파일 정보 저장 -> naru_detail 파싱
                .start(librarySyncStep)
                .next(libraryNaruDetailParsingStep)
                .listener(new LibrarySyncJobListener(libraryFileDownloader)) // 도서관 파일 저장 및 제거
                .validator(temp_FilePathJobParameterValidator.REQUIRED_FILE(customFilePathValidators, LIBRARY_FILE_PARAM_NAME))
                .build();
    }

    @Bean
    @JobScope
    public LibraryFileDownloader libraryFileDownloader(
            NaruRequestCreator requestCreator,
            NaruFileDownloader naruFileDownloader,
            @Value(LIBRARY_FILE_PARAM) String libraryDirPath) {
        return new LibraryFileDownloader(requestCreator, naruFileDownloader, libraryDirPath);
    }
}
