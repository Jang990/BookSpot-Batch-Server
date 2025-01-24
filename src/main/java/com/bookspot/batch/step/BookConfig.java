package com.bookspot.batch.step;

import com.bookspot.batch.data.file.csv.LibraryStockCsvData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BookConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final FlatFileItemReader<LibraryStockCsvData> bookStockCsvFileReader;
    private final ItemProcessor<LibraryStockCsvData, LibraryStockCsvData> isbnValidationProcessor;
    private final JdbcBatchItemWriter<LibraryStockCsvData> stockBookWriter;

    @Bean
    public Step bookUpdateStep() {
        return new StepBuilder("bookUpdateStep", jobRepository)
                .<LibraryStockCsvData, LibraryStockCsvData>chunk(BookStepConst.CHUNK_SIZE, platformTransactionManager)
                .reader(bookStockCsvFileReader)
                .processor(isbnValidationProcessor)
                .writer(stockBookWriter) // 쓰기 부분이 20~30초의 차이를 만들어 낸다.
//                .writer(items -> items.forEach(System.out::println))
                .build();
    }
}
