package com.bookspot.batch.step.reader;

import com.bookspot.batch.BookTestDataBuilder;
import com.bookspot.batch.SpringBootWithH2Test;
import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.step.service.BookRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootWithH2Test
class IsbnReaderTest {

    @Autowired DataSource dataSource;
    @Autowired IsbnIdPagingQueryProviderFactory isbnIdPagingQueryProviderFactory;
    private final int TEST_PAGE_SIZE = 10;
    IsbnReader isbnReader;

    @Autowired BookRepository bookRepository;
    @Autowired EntityManager em;

    @BeforeEach
    void beforeEach() throws Exception {
        bookRepository.saveAll(testBooks());
        em.flush();
        em.clear();

        isbnReader = new IsbnReader(
                dataSource,
                isbnIdPagingQueryProviderFactory.getObject(),
                TEST_PAGE_SIZE
        );

        isbnReader.afterPropertiesSet();
    }

    @Test
    void DB내에_존재하는_isbn13_정보를_가져옴() throws Exception {
        List<String> isbnList = List.of(isbnReader.read(), isbnReader.read());

        assertNull(isbnReader.read());
        assertEquals(2L, bookRepository.count());
        Assertions.assertThat(isbnList)
                .containsExactlyInAnyOrder(
                        "1234567890123",
                        "2345678901234"
                );
    }

    private static List<ConvertedUniqueBook> testBooks() {
        ConvertedUniqueBook book1 = new BookTestDataBuilder()
                .isbn("1234567890123")
                .build();
        ConvertedUniqueBook book2 = new BookTestDataBuilder()
                .isbn("2345678901234")
                .build();

        return List.of(book1, book2);
    }

}