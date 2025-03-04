package com.bookspot.batch.step.service;

import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniqueBookRepository extends JpaRepository<ConvertedUniqueBook, Long> {
}
