package com.bookspot.batch.step;

import com.bookspot.batch.data.Library;
import com.bookspot.batch.step.processor.csv.stock.repository.LibraryRepository;
import com.bookspot.batch.step.reader.LibraryExcelConst;
import com.bookspot.batch.step.reader.file.excel.library.LibraryExcelRowMapper;
import com.bookspot.batch.step.reader.file.excel.library.LibraryFileDownloader;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class LibraryStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    @Bean
    public Step librarySyncStep() {
        return new StepBuilder("librarySyncStep", jobRepository)
                .<Library, Library>chunk(LibraryStepConst.LIBRARY_CHUNK_SIZE, platformTransactionManager)
                .reader(libraryExcelReader())
                .writer(libraryWriter())
                .build();
    }

    @Bean
    public PoiItemReader<Library> libraryExcelReader() {
        PoiItemReader<Library> reader = new PoiItemReader<>();
        reader.setName("libraryExcelReader");
        reader.setResource(new FileSystemResource(LibraryExcelConst.metadata.absolutePath()));
        reader.setLinesToSkip(8);
        reader.setRowMapper(new LibraryExcelRowMapper());
        return reader;
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
