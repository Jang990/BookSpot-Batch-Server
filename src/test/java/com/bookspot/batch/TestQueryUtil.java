package com.bookspot.batch;

import com.bookspot.batch.data.Library;
import jakarta.persistence.EntityManager;

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
}
