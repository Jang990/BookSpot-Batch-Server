package com.bookspot.batch.step.writer;

import com.bookspot.batch.library.data.Library;
import com.bookspot.batch.library.data.LibraryNaruDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class LibraryWriterConfig {
    private final DataSource dataSource;

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
