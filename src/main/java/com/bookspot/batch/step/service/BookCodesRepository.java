package com.bookspot.batch.step.service;

import com.bookspot.batch.data.BookCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCodesRepository extends JpaRepository<BookCodes, Integer> {
}
