package com.bookspot.batch.step;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.global.file.stock.StockFilenameUtil;
import com.bookspot.batch.job.StockNormalizeJobConfig;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.partition.StockCsvPartitionConfig;
import com.bookspot.batch.step.processor.DuplicatedBookIdFilter;
import com.bookspot.batch.step.processor.StockProcessor;
import com.bookspot.batch.step.reader.StockCsvFileReader;
import com.bookspot.batch.step.service.memory.isbn.BookIdSet;
import com.bookspot.batch.step.writer.file.stock.StockNormalizeFileWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class StockNormalizeStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step stockNormalizeMasterStep(
            Step stockNormalizeStep,
            TaskExecutorPartitionHandler stockNormalizePartitionHandler) throws IOException {
        return new StepBuilder("stockNormalizeMasterStep", jobRepository)
                .partitioner(stockNormalizeStep.getName(), stockNormalizePartitioner(null))
                .partitionHandler(stockNormalizePartitionHandler)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler stockNormalizePartitionHandler(
            Step stockNormalizeStep,
            TaskExecutor multiTaskPool) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(stockNormalizeStep);
        partitionHandler.setTaskExecutor(multiTaskPool);
        return partitionHandler;
    }

    @Bean
    @StepScope
    public MultiResourcePartitioner stockNormalizePartitioner(
            @Value(StockNormalizeJobConfig.SOURCE_DIR_PARAM) String root) throws IOException {
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();

        Path rootPath = Paths.get(root);
        Resource[] resources = Files.list(rootPath) // 루트 디렉토리의 파일 리스트 가져오기
                .filter(Files::isRegularFile) // 파일만 필터링 (디렉토리 제외)
                .map(Path::toFile) // Path -> File 변환
                .map(FileSystemResource::new) // File -> Resource 변환
                .toArray(Resource[]::new);

        partitioner.setKeyName(StockCsvPartitionConfig.PARTITIONER_KEY);
        partitioner.setResources(resources);

        return partitioner;
    }

    @Bean
    public Step stockNormalizeStep(
            StockCsvFileReader stockCsvFileReader,
            StockProcessor stockProcessor,
            DuplicatedBookIdFilter duplicatedBookIdFilter,
            StockNormalizeFileWriter stockNormalizeFileWriter,
            StepLoggingListener stepLoggingListener) {
        return new StepBuilder("stockNormalizeStep", jobRepository)
                .<StockCsvData, LibraryStock>chunk(10_000, transactionManager)
                .reader(stockCsvFileReader)
                .processor(
                        new CompositeItemProcessor<>(
                                List.of(
                                        stockProcessor,
                                        duplicatedBookIdFilter
                                )
                        )
                )
                .writer(stockNormalizeFileWriter)
                .listener(stepLoggingListener)
                .build();
    }

    @Bean
    @StepScope
    public DuplicatedBookIdFilter duplicatedBookIdFilter() {
        return new DuplicatedBookIdFilter(new BookIdSet());
    }

    @Bean
    @StepScope
    public StockNormalizeFileWriter stockNormalizeFileWriter(
            @Value(StockCsvPartitionConfig.STEP_EXECUTION_FILE) Resource file,
            @Value(StockNormalizeJobConfig.NORMALIZE_DIR_PARAM) String normalizeDirPath) {
        System.out.println(file);
        System.out.println(normalizeDirPath);

        String outputFile = normalizeDirPath.concat("/")
                .concat(StockFilenameUtil.toNormalized(file.getFilename()))
                .concat(".csv");
        return new StockNormalizeFileWriter(outputFile);
    }
}
