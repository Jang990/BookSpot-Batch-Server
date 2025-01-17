package com.bookspot.batch.book;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String ISBN_QUERY = "SELECT id FROM Book WHERE isbn13 = ?";

    public Optional<Long> findIdByIsbn13(String isbn13) {
        try {
            return Optional.of(jdbcTemplate
                    .queryForObject(
                            ISBN_QUERY,
                            new Object[]{isbn13},
                            Long.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
