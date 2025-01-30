package com.bookspot.batch.step;

import com.bookspot.batch.data.file.csv.LibraryStockCsvData;
import com.bookspot.batch.step.service.IsbnMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BookConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final FlatFileItemReader<LibraryStockCsvData> bookStockCsvFileReader;
    private final ItemProcessor<LibraryStockCsvData, LibraryStockCsvData> isbnValidationProcessor;
    private final JdbcBatchItemWriter<LibraryStockCsvData> stockBookWriter;

    private final IsbnMemoryRepository isbnMemoryRepository;

    @Bean
    public Step bookSyncStep() {
        return new StepBuilder("bookSyncStep", jobRepository)
                .<LibraryStockCsvData, LibraryStockCsvData>chunk(BookStepConst.CHUNK_SIZE, platformTransactionManager)
                .reader(bookStockCsvFileReader)
                .processor(isbnValidationProcessor)
                .writer(compositeBookWriter()) // 쓰기 부분이 20~30초의 차이를 만들어 낸다.
//                .writer(items -> items.forEach(System.out::println))
                .build();
    }

    @Bean
    public CompositeItemWriter<LibraryStockCsvData> compositeBookWriter() {
        CompositeItemWriter<LibraryStockCsvData> writer = new CompositeItemWriter<>();
        writer.setDelegates(List.of(stockBookWriter, memoryIsbnWriter()));
        return writer;
    }

    @Bean
    public ItemWriter<LibraryStockCsvData> memoryIsbnWriter() {
        return new ItemWriter<LibraryStockCsvData>() {
            @Override
            public void write(Chunk<? extends LibraryStockCsvData> chunk) throws Exception {
                chunk.getItems().stream()
                        .map(LibraryStockCsvData::getIsbn)
                        .forEach(isbnMemoryRepository::add);
            }
        };
    }
}
