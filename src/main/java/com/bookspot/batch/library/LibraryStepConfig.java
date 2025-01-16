package com.bookspot.batch.library;

import com.bookspot.batch.library.data.Library;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
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

    private final LibraryItemReader libraryItemReader;

    @Bean
    public Step libraryStep() {
        return new StepBuilder(LibraryStepConst.STEP_NAME, jobRepository)
                .<Library, Library>chunk(LibraryStepConst.LIBRARY_CHUNK_SIZE, platformTransactionManager)
                .reader(libraryItemReader)
                .writer(libraryWriter())
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Library> libraryWriter() {
        return new JdbcBatchItemWriterBuilder<Library>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO library (name, library_code, location, updated_at) VALUES
                        (?, ?, ST_GeomFromText(CONCAT('POINT(', ?, ' ', ?, ')'), 4326), NOW())
                        ON DUPLICATE KEY UPDATE name = VALUES(name), location = VALUES(location), updated_at = NOW();
                        """)
                .itemPreparedStatementSetter(
                        (library, ps) -> {
                            ps.setString(1, library.getName());
                            ps.setString(2, library.getLibraryCode());
                            ps.setDouble(3, library.getLatitude());
                            ps.setDouble(4, library.getLongitude());
                        })
                .build();
    }

}
