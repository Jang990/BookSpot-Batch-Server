package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.TEMP_BookDocument;
import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.step.service.UniqueBookRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;
import java.util.stream.Collectors;

public class BookWithLibraryIdReader implements ItemReader<TEMP_BookDocument> {
    private static final String library_ids_query = """
            SELECT book_id, JSON_ARRAYAGG(library_id) as library_ids
            FROM library_stock
            WHERE book_id in (:bookIds)
            GROUP BY book_id;
            """;

    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final UniqueBookRepository bookRepository;
    private final ObjectMapper objectMapper;
    private final int pageSize;

    private List<TEMP_BookDocument> currentBatch;
    private int currentIndex;
    private int currentPage;

    @PostConstruct
    protected void postConstruct() {
        currentPage = 0;
        currentIndex = 0;
        currentBatch = new ArrayList<>(pageSize);
    }

    public BookWithLibraryIdReader(
            NamedParameterJdbcTemplate namedJdbcTemplate,
            UniqueBookRepository bookRepository,
            ObjectMapper objectMapper,
            int pageSize) {
        Objects.requireNonNull(namedJdbcTemplate);
        Objects.requireNonNull(bookRepository);
        Objects.requireNonNull(objectMapper);
        if(pageSize <= 0)
            throw new IllegalArgumentException();

        this.namedJdbcTemplate = namedJdbcTemplate;
        this.bookRepository = bookRepository;
        this.objectMapper = objectMapper;
        this.pageSize = pageSize;
    }

    @Override
    public TEMP_BookDocument read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(isAllDataFetched())
            return null;
        if(currentIndex >= currentBatch.size())
            fetchNextPage();
        return currentBatch.get(currentIndex++);
    }

    private void fetchNextPage() {
        List<ConvertedUniqueBook> content = bookRepository.findAll(PageRequest.of(currentPage, pageSize)).getContent();
        List<LibraryIds> libraryIds = queryLibraryIds(
                content.stream()
                        .mapToLong(ConvertedUniqueBook::getId)
                        .boxed()
                        .toList()
        );
        currentBatch = aggregate(content, libraryIds);

        currentIndex = 0;
        currentPage++;
    }

    private List<TEMP_BookDocument> aggregate(
            List<ConvertedUniqueBook> books,
            List<LibraryIds> libraryIds) {
        Map<Long, List<String>> libraryIdMap = libraryIds.stream()
                .collect(Collectors.toMap(LibraryIds::getBookId, LibraryIds::getLibraryIds));

        return books.stream()
                .map(book -> new TEMP_BookDocument(
                        book.getId().toString(),
                        book.getIsbn13(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublisher(),
                        book.getLoanCount(),
                        book.getSubjectCode(),
                        book.getPublicationYear() == null ? null : book.getPublicationYear().getValue(),
                        libraryIdMap.getOrDefault(book.getId(), new LinkedList<>())
                ))
                .collect(Collectors.toList());
    }

    private List<LibraryIds> queryLibraryIds(List<Long> bookIds) {
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

    private boolean isAllDataFetched() {
        return currentPage != 0
                && currentBatch.size() < pageSize
                && currentBatch.size() == currentIndex;
    }

    @Getter
    @RequiredArgsConstructor
    static class LibraryIds {
        private final long bookId;
        private final List<String> libraryIds;
    }
}
