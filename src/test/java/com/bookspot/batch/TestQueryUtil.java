package com.bookspot.batch;

import com.bookspot.batch.data.Library;
import com.bookspot.batch.data.LibraryStock;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class TestQueryUtil {
    public static List<Library> findLibraries(EntityManager em, List<String> libraryCodes, int limit) {
        return em.createQuery("""
                        SELECT l FROM
                        Library l
                        Where l.libraryCode IN :codes
                        """, Library.class)
                .setParameter("codes", libraryCodes)
                .setMaxResults(limit)
                .getResultList();
    }

    public static List<Library> findLibrariesByIds(EntityManager em, List<Long> libraryIds) {
        return em.createQuery("""
                        SELECT l FROM
                        Library l
                        Where l.id IN :ids
                        """, Library.class)
                .setParameter("ids", libraryIds)
                .getResultList();
    }

    public static List<LibraryStock> findStocks(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.query("""
                        SELECT library_id, book_id, created_at, updated_at_time
                        FROM library_stock
                        """, stockMapper()
        );
    }

    public static List<LibraryStock> findStocks(JdbcTemplate jdbcTemplate, long libraryId) {
        return jdbcTemplate.query("""
                        SELECT library_id, book_id, created_at, updated_at_time
                        FROM library_stock
                        WHERE library_id = ?
                        """, stockMapper(), libraryId
        );
    }

    private static RowMapper<LibraryStock> stockMapper() {
        return (rs, rowNum) -> {
            LibraryStock result = new LibraryStock(
                    rs.getLong("library_id"),
                    rs.getLong("book_id")
            );

            ReflectionTestUtils.setField(
                    result, "createdAt",
                    rs.getObject("created_at", LocalDate.class)
            );

            ReflectionTestUtils.setField(
                    result, "updatedAt",
                    rs.getObject("updated_at_time", LocalDateTime.class)
            );
            return result;
        };
    }
}
