package com.bookspot.batch.step;

import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.job.BookSyncJobConfig;
import com.bookspot.batch.step.listener.InvalidIsbn13LoggingListener;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.partition.StockCsvPartitionConfig;
import com.bookspot.batch.step.processor.StockCsvToBookConvertor;
import com.bookspot.batch.step.processor.IsbnValidationFilter;
import com.bookspot.batch.step.processor.InMemoryIsbnFilter;
import com.bookspot.batch.step.processor.TitleEllipsisConverter;
import com.bookspot.batch.step.processor.exception.InvalidIsbn13Exception;
import com.bookspot.batch.step.reader.StockCsvFileReader;
import com.bookspot.batch.step.writer.book.UniqueBookInfoWriter;
import com.bookspot.batch.step.writer.memory.InMemoryIsbnWriterWithCsv;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
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
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class BookSyncStepConfig {
    private static final int CHUNK_SIZE = 5_000;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final StepLoggingListener stepLoggingListener;
    private final InvalidIsbn13LoggingListener invalidIsbn13Listener;

    @Bean
    public Step bookSyncPartitionMasterStep(
            Step bookSyncStep,
            TaskExecutorPartitionHandler bookSyncPartitionHandler) throws IOException {
        return new StepBuilder("bookSyncPartitionMasterStep", jobRepository)
                .partitioner(bookSyncStep.getName(), bookSyncCsvPartitioner(null))
                .partitionHandler(bookSyncPartitionHandler)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler bookSyncPartitionHandler(Step bookSyncStep) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(bookSyncStep);
        partitionHandler.setTaskExecutor(new SyncTaskExecutor());
        return partitionHandler;
    }

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
                .listener(invalidIsbn13Listener)
                .listener(stepLoggingListener)
                .faultTolerant()
                .skip(InvalidIsbn13Exception.class)
                .skipLimit(50_000)
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
