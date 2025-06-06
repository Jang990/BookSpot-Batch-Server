package com.bookspot.batch.step;

import com.bookspot.batch.data.LoanCount;
import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.global.config.TaskExecutorConfig;
import com.bookspot.batch.job.loan.LoanAggregatedJobConfig;
import com.bookspot.batch.step.listener.LoanCountStepListener;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.partition.StockCsvPartitionConfig;
import com.bookspot.batch.step.processor.IsbnValidationFilter;
import com.bookspot.batch.step.processor.exception.InvalidIsbn13Exception;
import com.bookspot.batch.step.reader.IsbnIdReader;
import com.bookspot.batch.step.reader.StockCsvFileReader;
import com.bookspot.batch.step.service.memory.loan.LoanCountService;
import com.bookspot.batch.step.service.AggregatedBooksCsvWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
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
public class ReadLoanCountStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final LoanCountService loanCountService;
    private final StepLoggingListener stepLoggingListener;

    private static final int CHUNK_SIZE = 1000;

    @Bean
    public Step readLoanCountMasterStep(
            LoanCountStepListener loanCountStepListener,
            Step readLoanCountStep,
            TaskExecutorPartitionHandler loanCountPartitionHandler) throws IOException {
        return new StepBuilder("readLoanCountMasterStep", jobRepository)
                .listener(loanCountStepListener)
                .partitioner(readLoanCountStep.getName(), loanCountPartitioner(null))
                .partitionHandler(loanCountPartitionHandler)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    @StepScope
    public LoanCountStepListener loanCountStepListener(
            LoanCountService loanCountService,
            IsbnIdReader isbnIdReader,
            AggregatedBooksCsvWriter aggregatedBooksCsvWriter
    ) {
        return new LoanCountStepListener(
                loanCountService,
                isbnIdReader,
                aggregatedBooksCsvWriter
        );
    }

    @Bean
    @StepScope
    public AggregatedBooksCsvWriter aggregatedBooksCsvWriter(
            @Value(LoanAggregatedJobConfig.OUTPUT_FILE_PATH) String aggregatedFilePath,
            LoanCountService loanCountService
    ) {
        return new AggregatedBooksCsvWriter(aggregatedFilePath, loanCountService);
    }

    @Bean
    public Step readLoanCountStep(
            StockCsvFileReader stockCsvFileReader,
            IsbnValidationFilter isbnValidationFilter) {
        return new StepBuilder("readLoanCountStep", jobRepository)
                .<StockCsvData, LoanCount>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(stockCsvFileReader)
                .processor(
                    new CompositeItemProcessor<>(
                            List.of(
                                    isbnValidationFilter,
                                    loanCountConvertor(),
                                    memoryIsbnFilter()
                            )
                    )
                )
                .writer(
                        chunk -> {
                            for (LoanCount item : chunk.getItems())
                                loanCountService.increase(item.isbn13(), item.loanCount());
                        }
                )
                .listener(stepLoggingListener)
                .faultTolerant()
                .skip(InvalidIsbn13Exception.class)
                .skipLimit(200)
                .build();
    }

    @Bean
    public ItemProcessor<StockCsvData, LoanCount> loanCountConvertor() {
        return item -> new LoanCount(item.getIsbn(), item.getLoanCount());
    }

    @Bean
    public ItemProcessor<LoanCount, LoanCount> memoryIsbnFilter() {
        return item -> {
            if (!loanCountService.contains(item.isbn13()))
                return null;
            return item;
        };
    }

    @Bean
    public TaskExecutorPartitionHandler loanCountPartitionHandler(
            Step readLoanCountStep,
            TaskExecutor multiTaskPool) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(readLoanCountStep);
        partitionHandler.setTaskExecutor(multiTaskPool);
        partitionHandler.setGridSize(TaskExecutorConfig.MULTI_POOL_SIZE);
        return partitionHandler;
    }

    @Bean
    @StepScope
    public MultiResourcePartitioner loanCountPartitioner(
            @Value(LoanAggregatedJobConfig.DIRECTORY_PATH) String sourceDir) throws IOException {
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
