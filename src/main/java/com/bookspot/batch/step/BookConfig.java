package com.bookspot.batch.step;

import com.bookspot.batch.data.file.csv.LibraryStockCsvData;
import com.bookspot.batch.step.processor.csv.stock.IsbnValidationProcessor;
import com.bookspot.batch.step.processor.csv.stock.repository.BookRepository;
import com.bookspot.batch.step.service.Isbn13MemoryData;
import com.bookspot.batch.step.service.IsbnMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BookConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    private final FlatFileItemReader<LibraryStockCsvData> bookStockCsvFileReader;
    private final IsbnValidationProcessor isbnValidationProcessor;

    private final IsbnMemoryRepository isbnMemoryRepository;
    private final BookRepository bookRepository;

    @Bean
    public Step bookSyncStep() {
        return new StepBuilder("bookSyncStep", jobRepository)
                .<LibraryStockCsvData, LibraryStockCsvData>chunk(BookStepConst.CHUNK_SIZE, platformTransactionManager)
                .reader(bookStockCsvFileReader)
                .processor(isbnValidationProcessor)
                .writer(compositeBookWriter())
                .build();
    }

    @Bean
    public CompositeItemWriter<LibraryStockCsvData> compositeBookWriter() {
        CompositeItemWriter<LibraryStockCsvData> writer = new CompositeItemWriter<>();
        writer.setDelegates(
                List.of(stockBookWriter(), memoryIsbnWriter()));
        return writer;
    }

    // 도서관 재고 csv -> book 테이블 저장
    @Bean
    public JdbcBatchItemWriter<LibraryStockCsvData> stockBookWriter() {
        JdbcBatchItemWriter<LibraryStockCsvData> writer = new JdbcBatchItemWriterBuilder<LibraryStockCsvData>()
                .dataSource(dataSource)
                .sql("""
                        INSERT IGNORE INTO book
                        (isbn13, title, classification, volume_name)
                        VALUES(?, ?, ?, ?);
                        """)
                .itemPreparedStatementSetter(
                        (book, ps) -> {
                            ps.setString(1, book.getIsbn());
                            ps.setString(2, book.getTitle());
                            ps.setString(3, book.getSubjectCode());
                            ps.setString(4, book.getVolume());
                        })
                .assertUpdates(false)
                .build();
        return writer;
    }

    // 새로 등록된 책을 메모리에 등록
    @Bean
    public ItemWriter<LibraryStockCsvData> memoryIsbnWriter() {
        return chunk -> chunk.getItems().stream()
                .map(LibraryStockCsvData::getIsbn)
                .forEach(isbn13 -> {
                    Long bookId = bookRepository.findIdByIsbn13(isbn13)
                            .orElseThrow(IllegalArgumentException::new);
                    isbnMemoryRepository.add(new Isbn13MemoryData(isbn13, bookId));
                });
    }
}
