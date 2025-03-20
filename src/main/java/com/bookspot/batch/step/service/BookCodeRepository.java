package com.bookspot.batch.step.service;

import com.bookspot.batch.data.BookCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCodeRepository extends JpaRepository<BookCode, Integer> {
}
