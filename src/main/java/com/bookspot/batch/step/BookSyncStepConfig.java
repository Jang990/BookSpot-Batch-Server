package com.bookspot.batch.step;

import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.StockCsvToBookConvertor;
import com.bookspot.batch.step.processor.IsbnValidationProcessor;
import com.bookspot.batch.step.processor.InMemoryIsbnFilter;
import com.bookspot.batch.step.reader.StockCsvFileReader;
import com.bookspot.batch.step.writer.book.UniqueBookInfoWriter;
import com.bookspot.batch.step.writer.memory.InMemoryIsbnWriterWithCsv;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BookSyncStepConfig {
    private static final int CHUNK_SIZE = 5_000;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step bookSyncStep(
            StockCsvFileReader stockCsvFileReader,
            CompositeItemProcessor<StockCsvData, ConvertedUniqueBook> bookSyncProcessor,
            CompositeItemWriter<ConvertedUniqueBook> bookSyncItemWriter) {
        return new StepBuilder("bookSyncStep", jobRepository)
                .<StockCsvData, ConvertedUniqueBook>chunk(CHUNK_SIZE, transactionManager)
                .reader(stockCsvFileReader)
                .processor(bookSyncProcessor)
                .writer(bookSyncItemWriter)
                .build();
    }

    @Bean
    public CompositeItemProcessor<StockCsvData, ConvertedUniqueBook> bookSyncProcessor(
            IsbnValidationProcessor isbnValidationProcessor,
            InMemoryIsbnFilter inMemoryIsbnFilter,
            StockCsvToBookConvertor stockCsvToBookConvertor) {
        return new CompositeItemProcessor<>(
                isbnValidationProcessor,
                inMemoryIsbnFilter,
                stockCsvToBookConvertor
        );
    }

    @Bean
    public CompositeItemWriter<ConvertedUniqueBook> bookSyncItemWriter(
            UniqueBookInfoWriter uniqueBookInfoWriter,
            InMemoryIsbnWriterWithCsv inMemoryIsbnWriterWithCsv) {
        return new CompositeItemWriter<>(
                uniqueBookInfoWriter,
                inMemoryIsbnWriterWithCsv
        );
    }
}
