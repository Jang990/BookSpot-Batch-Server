package com.bookspot.batch.step;

import com.bookspot.batch.data.crawler.LibraryNaruDetail;
import com.bookspot.batch.step.reader.LibraryNaruDetailReader;
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
public class LibraryNaruDetailParsingStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    private final LibraryNaruDetailReader naruDetailReader;

    @Bean
    public Step libraryNaruDetailParsingStep() {
        return new StepBuilder("libraryNaruDetailParsingStep", jobRepository)
                .<LibraryNaruDetail, LibraryNaruDetail>chunk(LibraryStepConst.LIBRARY_CHUNK_SIZE, platformTransactionManager)
                .reader(naruDetailReader)
                .writer(naruDetailWriter())
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<LibraryNaruDetail> naruDetailWriter() {
        return new JdbcBatchItemWriterBuilder<LibraryNaruDetail>()
                .dataSource(dataSource)
                .sql("""
                        UPDATE library
                        SET naru_detail = ?
                        WHERE address = ? AND name = ?
                        """)
                .itemPreparedStatementSetter(
                        (naruDetail, ps) -> {
                            ps.setString(1, naruDetail.naruDetail());
                            ps.setString(2, naruDetail.address());
                            ps.setString(3, naruDetail.name());
                        })
                .build();
    }
}
