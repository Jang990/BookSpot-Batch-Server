package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.step.service.BookRepository;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.data.domain.Sort;

import java.util.Collections;

public class BookRepositoryReader extends RepositoryItemReader<ConvertedUniqueBook> {
    public BookRepositoryReader(
            BookRepository repository,
            final int pageSize) {
        setName("bookReader");
        setRepository(repository);
        setMethodName("findAll");
        setPageSize(pageSize);
        setSort(Collections.singletonMap("loanCount", Sort.Direction.DESC));
    }
}
