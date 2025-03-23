package com.bookspot.batch.step;

import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.processor.StockCsvToBookConvertor;
import com.bookspot.batch.step.processor.IsbnValidationFilter;
import com.bookspot.batch.step.processor.InMemoryIsbnFilter;
import com.bookspot.batch.step.processor.TitleEllipsisConverter;
import com.bookspot.batch.step.reader.TempStockCsvFileReader;
import com.bookspot.batch.step.writer.book.UniqueBookInfoWriter;
import com.bookspot.batch.step.writer.memory.InMemoryIsbnWriterWithCsv;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BookSyncStepConfig {
    private static final int CHUNK_SIZE = 5_000;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step bookSyncPartitionMasterStep(
            Step bookSyncStep,
            MultiResourcePartitioner stockCsvPartitioner,
            TaskExecutorPartitionHandler stockCsvPartitionHandler) {
        return new StepBuilder("bookSyncPartitionMasterStep", jobRepository)
                .partitioner(bookSyncStep.getName(), stockCsvPartitioner)
                .partitionHandler(stockCsvPartitionHandler)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler stockCsvPartitionHandler(Step bookSyncStep, TaskExecutor stockCsvTaskPool) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(bookSyncStep);
        partitionHandler.setTaskExecutor(stockCsvTaskPool);
        return partitionHandler;
    }

    @Bean
    public Step bookSyncStep(
            TempStockCsvFileReader stockCsvFileReader,
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
            IsbnValidationFilter isbnValidationFilter,
            InMemoryIsbnFilter inMemoryIsbnFilter,
            TitleEllipsisConverter titleEllipsisConverter,
            StockCsvToBookConvertor stockCsvToBookConvertor) {
        return new CompositeItemProcessor<>(
                isbnValidationFilter,
                inMemoryIsbnFilter,
                titleEllipsisConverter,
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
