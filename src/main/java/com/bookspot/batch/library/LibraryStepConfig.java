package com.bookspot.batch.library;

import com.bookspot.batch.library.data.Library;
import com.bookspot.batch.library.data.LibraryNaruDetail;
import com.bookspot.batch.library.naru.LibraryNaruDetailReader;
import com.bookspot.batch.library.reader.file.LibraryFileDownloader;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

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

    @Bean
    public Step libraryNaruDetailStep() {
        return new StepBuilder(LibraryStepConst.NARU_DETAIL_STEP_NAME, jobRepository)
                .<LibraryNaruDetail, LibraryNaruDetail>chunk(LibraryStepConst.LIBRARY_CHUNK_SIZE, platformTransactionManager)
                .reader(naruDetailReader)
                .writer(libraryNaruDetailWriter)
                .build();
    }

    @Bean
    public Step libraryStep() {
        return new StepBuilder(LibraryStepConst.STEP_NAME, jobRepository)
                .<Library, Library>chunk(LibraryStepConst.LIBRARY_CHUNK_SIZE, platformTransactionManager)
                .reader(libraryExcelReader)
                .writer(libraryWriter)
                .build();
    }

    @Bean
    public Step libraryExcelDownloadStep() {
        return new StepBuilder(LibraryStepConst.FILE_DOWNLOAD_STEP_NAME, jobRepository)
                .tasklet(libraryExcelDownloadTasklet(), platformTransactionManager)
                .build();
    }

    @Bean
    public Step libraryExcelDeleteStep() {
        return new StepBuilder(LibraryStepConst.FILE_DELETE_STEP_NAME, jobRepository)
                .tasklet(libraryExcelDeleteTasklet(), platformTransactionManager)
                .build();
    }

    @Bean
    public MethodInvokingTaskletAdapter libraryExcelDownloadTasklet() {
        MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();
        adapter.setTargetObject(downloader);
        adapter.setTargetMethod("download");
        return adapter;
    }

    @Bean
    public MethodInvokingTaskletAdapter libraryExcelDeleteTasklet() {
        MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();
        adapter.setTargetObject(downloader);
        adapter.setTargetMethod("delete");
        return adapter;
    }

}
