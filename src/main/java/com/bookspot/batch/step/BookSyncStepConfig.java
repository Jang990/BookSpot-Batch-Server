package com.bookspot.batch.step;

import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.global.config.TaskExecutorConfig;
import com.bookspot.batch.global.file.stock.StockFilenameUtil;
import com.bookspot.batch.job.BookSyncJobConfig;
import com.bookspot.batch.step.listener.BookSyncStepListener;
import com.bookspot.batch.step.listener.InvalidIsbn13LoggingListener;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.listener.alert.AlertStepListener;
import com.bookspot.batch.step.partition.StockCsvPartitionConfig;
import com.bookspot.batch.step.processor.StockCsvToBookConvertor;
import com.bookspot.batch.step.processor.IsbnValidationFilter;
import com.bookspot.batch.step.processor.InMemoryIsbnFilter;
import com.bookspot.batch.step.processor.TitleEllipsisConverter;
import com.bookspot.batch.step.processor.csv.IsbnValidator;
import com.bookspot.batch.step.processor.exception.InvalidIsbn13Exception;
import com.bookspot.batch.step.reader.IsbnReader;
import com.bookspot.batch.step.reader.StockCsvFileReader;
import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import com.bookspot.batch.step.writer.book.UniqueBookInfoWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class BookSyncStepConfig {
    private static final int CHUNK_SIZE = 1_300;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final StepLoggingListener stepLoggingListener;
    private final InvalidIsbn13LoggingListener invalidIsbn13Listener;

    @Bean
    public Step bookSyncPartitionMasterStep(
            Step bookSyncStep,
            AlertStepListener alertStepListener,
            BookSyncStepListener bookSyncStepListener,
            TaskExecutorPartitionHandler bookSyncPartitionHandler) throws IOException {
        return new StepBuilder("bookSyncPartitionMasterStep", jobRepository)
                .listener(bookSyncStepListener)
                .listener(alertStepListener)
                .partitioner(bookSyncStep.getName(), bookSyncCsvPartitioner(null))
                .partitionHandler(bookSyncPartitionHandler)
                .build();
    }

    @Bean
    @JobScope
    public BookSyncStepListener bookSyncStepListener(IsbnReader isbnReader, IsbnSet isbnSet) {
        return new BookSyncStepListener(isbnReader, isbnSet);
    }

    @Bean
    public TaskExecutorPartitionHandler bookSyncPartitionHandler(
            Step bookSyncStep,
            TaskExecutor multiTaskPool) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(bookSyncStep);
        partitionHandler.setTaskExecutor(multiTaskPool);
        partitionHandler.setGridSize(TaskExecutorConfig.MULTI_POOL_SIZE);
        return partitionHandler;
    }

    @Bean
    public Step bookSyncStep(
            StockCsvFileReader stockCsvFileReader,
            CompositeItemProcessor<StockCsvData, ConvertedUniqueBook> bookSyncProcessor,
            UniqueBookInfoWriter uniqueBookInfoWriter) {
        return new StepBuilder("bookSyncStep", jobRepository)
                .<StockCsvData, ConvertedUniqueBook>chunk(CHUNK_SIZE, transactionManager)
                .reader(stockCsvFileReader)
                .processor(bookSyncProcessor)
                .writer(uniqueBookInfoWriter)
                .listener(invalidIsbn13Listener)
                .listener(stepLoggingListener)
                .faultTolerant()
                .skip(InvalidIsbn13Exception.class)
                .skipLimit(200)
                .retry(CannotAcquireLockException.class)
                .retryLimit(20)
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
    @StepScope
    public IsbnValidationFilter isbnValidationFilter(
            IsbnValidator isbnValidator,
            @Value(StockCsvPartitionConfig.STEP_EXECUTION_FILE) Resource file
    ) {
        return new IsbnValidationFilter(
                isbnValidator,
                file
        );
    }

    @Bean
    @StepScope
    public MultiResourcePartitioner bookSyncCsvPartitioner(
            @Value(BookSyncJobConfig.SOURCE_DIR_PARAM) String sourceDir) throws IOException {
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();

        Path rootPath = Paths.get(sourceDir);
        Resource[] resources = Files.list(rootPath) // 루트 디렉토리의 파일 리스트 가져오기
                .filter(Files::isRegularFile) // 파일만 필터링 (디렉토리 제외)
                .map(Path::toFile) // Path -> File 변환
                .map(FileSystemResource::new) // File -> Resource 변환
                .toArray(Resource[]::new);

        partitioner.setKeyName(StockCsvPartitionConfig.PARTITIONER_KEY);
        partitioner.setResources(resources);

        return partitioner;
    }
}
