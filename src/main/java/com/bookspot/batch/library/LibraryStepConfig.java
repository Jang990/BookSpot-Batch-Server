package com.bookspot.batch.library;

import com.bookspot.batch.library.data.Library;
import com.bookspot.batch.library.file.LibraryFileDownloader;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class LibraryStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    private final PoiItemReader<Library> libraryExcelReader;
    private final LibraryFileDownloader downloader;

    @Bean
    public Step libraryStep() {
        return new StepBuilder(LibraryStepConst.STEP_NAME, jobRepository)
                .<Library, Library>chunk(LibraryStepConst.LIBRARY_CHUNK_SIZE, platformTransactionManager)
                .reader(libraryExcelReader)
                .writer(libraryWriter())
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

    @Bean
    public JdbcBatchItemWriter<Library> libraryWriter() {
        return new JdbcBatchItemWriterBuilder<Library>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO library (name, library_code, location, address, updated_at) VALUES
                        (?, ?, ST_GeomFromText(CONCAT('POINT(', ?, ' ', ?, ')'), 4326), ?, NOW())
                        ON DUPLICATE KEY UPDATE name = VALUES(name), location = VALUES(location), address = VALUES(address), updated_at = NOW();
                        """)
                .itemPreparedStatementSetter(
                        (library, ps) -> {
                            ps.setString(1, library.getName());
                            ps.setString(2, library.getLibraryCode());
                            ps.setDouble(3, library.getLatitude());
                            ps.setDouble(4, library.getLongitude());
                            ps.setString(5, library.getAddress());
                        })
                .build();
    }

}
