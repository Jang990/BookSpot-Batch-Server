package com.bookspot.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobConfig {
    private final JobRepository jobRepository;


    @Bean
    public Job bookSpot(Step stockCsvDownloadStep) {
        return new JobBuilder("bookSpot", jobRepository)

                // 도서관 파일 다운로드 -> 도서관 파일 정보 저장 -> 파일 삭제 -> naru_detail 파싱
                /*.start(getStep(LibraryStepConst.FILE_DOWNLOAD_STEP_NAME))
                .next(getStep(LibraryStepConst.STEP_NAME))
                .next(getStep(LibraryStepConst.FILE_DELETE_STEP_NAME))
                .next(getStep(LibraryStepConst.NARU_DETAIL_STEP_NAME))*/

                // naru_detail이 있는 도서관 파일 다운로드
                .start(stockCsvDownloadStep)
                .build();
    }

    @Bean
    public Job stockFileJob(Step bookUpdateStep, Step libraryStockUpdateStep) {
        // 책 업데이트 -> 재고 업데이트 -> 파일 삭제 -> 크롤링 시점 업데이트
        return new JobBuilder("stockFileJob", jobRepository)
                .start(bookUpdateStep)
                .next(libraryStockUpdateStep)
                .build();
    }

}
