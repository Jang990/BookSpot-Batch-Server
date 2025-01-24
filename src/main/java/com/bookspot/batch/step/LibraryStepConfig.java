package com.bookspot.batch.step;

import com.bookspot.batch.data.Library;
import com.bookspot.batch.data.crawler.LibraryNaruDetail;
import com.bookspot.batch.step.processor.csv.stock.repository.LibraryRepository;
import com.bookspot.batch.step.reader.LibraryNaruDetailReader;
import com.bookspot.batch.step.reader.file.excel.library.LibraryFileDownloader;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class LibraryStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final LibraryFileDownloader downloader;
    private final PoiItemReader<Library> libraryExcelReader;
    private final JdbcBatchItemWriter<Library> libraryWriter;

    private final LibraryNaruDetailReader naruDetailReader;
    private final JdbcBatchItemWriter<LibraryNaruDetail> libraryNaruDetailWriter;

    private final LibraryRepository libraryRepository;

    @Bean
    public Step libraryNaruDetailParsingStep() {
        return new StepBuilder("libraryNaruDetailParsingStep", jobRepository)
                .<LibraryNaruDetail, LibraryNaruDetail>chunk(LibraryStepConst.LIBRARY_CHUNK_SIZE, platformTransactionManager)
                .reader(naruDetailReader)
                .writer(libraryNaruDetailWriter)
                .build();
    }

    @Bean
    public Step librarySyncStep() {
        return new StepBuilder("librarySyncStep", jobRepository)
                .<Library, Library>chunk(LibraryStepConst.LIBRARY_CHUNK_SIZE, platformTransactionManager)
                .reader(libraryExcelReader)
                .writer(libraryWriter)
                .build();
    }

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

    @Bean
    public Step libraryStockUpdatedAtStep() {
        return new StepBuilder("libraryStockUpdatedAtStep", jobRepository)
                .tasklet(libraryStockUpdatedAtTasklet(null, null), platformTransactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet libraryStockUpdatedAtTasklet(
            @Value("#{jobParameters['libraryId']}") Long libraryId,
            @Value("#{jobParameters['referenceDate']}") LocalDate referenceDate) {
        return (contribution, chunkContext) -> {
            libraryRepository.updateStockDate(libraryId, referenceDate);
            return RepeatStatus.FINISHED;
        };
    }

}
