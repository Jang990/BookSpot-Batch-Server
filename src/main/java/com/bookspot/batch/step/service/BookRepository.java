package com.bookspot.batch.step.service;

import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<ConvertedUniqueBook, Long> {
    List<ConvertedUniqueBook> findByIsbn13In(List<String> isbn13);
}
