package com.bookspot.batch.step.service;

import com.bookspot.batch.data.LibraryIds;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LibraryStockRepository {
    private static final String library_ids_query = """
            SELECT book_id, JSON_ARRAYAGG(library_id) as library_ids
            FROM library_stock
            WHERE book_id in (:bookIds)
            GROUP BY book_id;
            """;

    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final ObjectMapper objectMapper;

    public List<LibraryIds> findLibraryIds(List<Long> bookIds) {
        if(bookIds.isEmpty())
            return Collections.emptyList();

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("bookIds", bookIds);

        return namedJdbcTemplate.query(
                library_ids_query,
                parameters,
                (rs, rowNum) -> {
                    try {
                        return new LibraryIds(
                                rs.getLong("book_id"),
                                objectMapper.readValue(rs.getString("library_ids"), new TypeReference<>() {})
                        );
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}
