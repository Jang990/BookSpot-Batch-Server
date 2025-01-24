package com.bookspot.batch.step;

import com.bookspot.batch.data.LibraryForFileParsing;
import com.bookspot.batch.data.crawler.StockFileData;
import com.bookspot.batch.step.processor.crawler.StockFilePathParser;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class StockCsvDownloadStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final JdbcPagingItemReader<LibraryForFileParsing> libraryForFileParsingReader;
    private final StockFilePathParser stockFilePathParser;
    private final ItemWriter<StockFileData> stockFileDownloaderWriter;

    @Bean
    public Step stockCsvDownloadStep() {
        return new StepBuilder(StockStepConst.DOWNLOAD_STEP_NAME, jobRepository)
                .<LibraryForFileParsing, StockFileData>chunk(StockStepConst.DOWNLOAD_CHUNK_SIZE, platformTransactionManager)
                .reader(libraryForFileParsingReader)
                .processor(stockFilePathParser)
                .writer(stockFileDownloaderWriter)
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
    public Tasklet stockCsvDeleteTasklet(@Value("#{jobParameters['filePath']}") String filePath) {
        return (contribution, chunkContext) -> {
            Files.delete(Paths.get(filePath)); // 파일 삭제
            return RepeatStatus.FINISHED;
        };
    }

}
