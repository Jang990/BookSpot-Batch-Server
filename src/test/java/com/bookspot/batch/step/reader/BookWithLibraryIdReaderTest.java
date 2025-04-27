package com.bookspot.batch.step.reader;

import com.bookspot.batch.TestInsertUtils;
import com.bookspot.batch.step.service.LibraryStockRepository;
import com.bookspot.batch.step.service.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BookWithLibraryIdReaderTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    BookRepository bookRepository;
    @Autowired LibraryStockRepository libraryStockRepository;

    private void initData() {
        TestInsertUtils.bookBuilder().id(1).insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(2).insert(jdbcTemplate);
        TestInsertUtils.bookBuilder().id(3).insert(jdbcTemplate);

        TestInsertUtils.libraryBuilder().id(1).insert(jdbcTemplate);
        TestInsertUtils.libraryBuilder().id(2).insert(jdbcTemplate);
        TestInsertUtils.libraryBuilder().id(3).insert(jdbcTemplate);

        TestInsertUtils.libraryStockBuilder()
                .bookId(1).libraryId(1).insert(jdbcTemplate);
        TestInsertUtils.libraryStockBuilder()
                .bookId(1).libraryId(2).insert(jdbcTemplate);

        TestInsertUtils.libraryStockBuilder()
                .bookId(2).libraryId(2).insert(jdbcTemplate);
        TestInsertUtils.libraryStockBuilder()
                .bookId(2).libraryId(3).insert(jdbcTemplate);
    }

    @Test
    void 정상처리() throws Exception {
        initData();
        BookWithLibraryIdReader reader = new BookWithLibraryIdReader(
                bookRepository, libraryStockRepository, 2
        );
        ExecutionContext ec = new ExecutionContext();
        reader.open(ec);

        assertThat(reader.read().getLibraryIdsArrayString())
                .containsExactlyInAnyOrder("1", "2");
        assertThat(reader.read().getLibraryIdsArrayString())
                .containsExactlyInAnyOrder("2", "3");

        assertTrue(reader.read().getLibraryIdsArrayString().isEmpty());
        assertNull(reader.read());

        assertEquals(ec.get(BookWithLibraryIdReader.KEY_PAGE), 2);
    }

    @Test
    void 진행된_페이지_불러오기_가능() throws Exception {
        initData();
        BookWithLibraryIdReader reader = new BookWithLibraryIdReader(
                bookRepository, libraryStockRepository, 2
        );

        ExecutionContext ec = new ExecutionContext();
        ec.put(BookWithLibraryIdReader.KEY_PAGE, 1);

        reader.open(ec);

        assertTrue(reader.read().getLibraryIdsArrayString().isEmpty());
        assertNull(reader.read());

        assertEquals(ec.get(BookWithLibraryIdReader.KEY_PAGE), 2);
    }

    @Test
    void 데이터가_없는_경우() throws Exception {
        BookWithLibraryIdReader reader = new BookWithLibraryIdReader(
                bookRepository, libraryStockRepository, 2
        );

        ExecutionContext ec = new ExecutionContext();
        reader.open(ec);

        assertNull(reader.read());

        assertEquals(ec.get(BookWithLibraryIdReader.KEY_PAGE), 0);
    }
}