package com.bookspot.batch;

import com.bookspot.batch.data.Library;
import com.bookspot.batch.data.LibraryStock;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Date;
import java.time.LocalDate;
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
                        SELECT library_id, book_id, updated_at
                        FROM library_stock
                        """, stockMapper()
        );
    }

    private static RowMapper<LibraryStock> stockMapper() {
        return (rs, rowNum) -> {
            LibraryStock result = new LibraryStock(
                    rs.getLong("library_id"),
                    rs.getLong("book_id")
            );

            ReflectionTestUtils.setField(
                    result, "updatedAt",
                    rs.getObject("updated_at", LocalDate.class)
            );
            return result;
        };
    }
}
