package com.bookspot.batch;

import com.bookspot.batch.data.Library;
import com.bookspot.batch.data.LibraryStock;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;

public class TestQueryUtil {
    public static List<Library> findLibraries(EntityManager em, List<String> libraryCodes) {
        return em.createQuery("""
                        SELECT l FROM
                        Library l
                        Where l.libraryCode IN :codes
                        """, Library.class)
                .setParameter("codes", libraryCodes)
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
                        SELECT library_id, book_id
                        FROM library_stock
                        """,
                (rs, rowNum) -> new LibraryStock(
                        rs.getLong("library_id"),
                        rs.getLong("book_id")
                )
        );
    }
}
