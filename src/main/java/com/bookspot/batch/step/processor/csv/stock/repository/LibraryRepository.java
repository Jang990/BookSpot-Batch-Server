package com.bookspot.batch.step.processor.csv.stock.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LibraryRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String LIBRARY_CODE_QUERY = "SELECT id FROM library WHERE library_code = ?";
    private static final String LIBRARY_STOCK_UPDATED_AT_QUERY = "UPDATE library SET stock_updated_at = ? WHERE id = ?";

    public Optional<Long> findId(String libraryCode) {
        try {
            return Optional.of(jdbcTemplate
                    .queryForObject(
                            LIBRARY_CODE_QUERY,
                            new Object[]{libraryCode},
                            Long.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updateStockDate(long libraryId, LocalDate stockDate) {
        jdbcTemplate.update(LIBRARY_STOCK_UPDATED_AT_QUERY, stockDate, libraryId);
    }
}
