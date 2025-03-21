package com.bookspot.batch.job;

import com.bookspot.batch.job.listener.LibrarySyncJobListener;
import com.bookspot.batch.step.reader.file.excel.library.LibraryFileDownloader;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LibrarySyncJobConfig {
    private final JobRepository jobRepository;

    @Bean
    public Job librarySyncJob(
            LibraryFileDownloader libraryFileDownloader,
            Step librarySyncStep,
            Step libraryNaruDetailParsingStep,
            Step stockCsvDownloadStep) {
        return new JobBuilder("librarySyncJob", jobRepository)
                // 도서관 파일 정보 저장 -> naru_detail 파싱
                .start(librarySyncStep)
                .next(libraryNaruDetailParsingStep)

                // naru_detail이 있는 도서관 파일 다운로드
                .next(stockCsvDownloadStep)
                .listener(new LibrarySyncJobListener(libraryFileDownloader)) // 도서관 파일 저장 및 제거
                .build();
    }
}
