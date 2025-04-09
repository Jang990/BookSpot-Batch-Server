package com.bookspot.batch.step;

import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.job.LoanAggregatedJobConfig;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.partition.StockCsvPartitionConfig;
import com.bookspot.batch.step.processor.exception.InvalidIsbn13Exception;
import com.bookspot.batch.step.reader.StockCsvFileReader;
import com.bookspot.batch.step.service.memory.loan.InMemoryLoanCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
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

@Configuration
@RequiredArgsConstructor
public class ReadLoanCountStepConfig {
    private static final int CHUNK_SIZE = 5_000;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final InMemoryLoanCountService bookService;
    private final StepLoggingListener stepLoggingListener;

    @Bean
    public Step readLoanCountMasterStep(
            Step readLoanCountStep,
            TaskExecutorPartitionHandler loanCountPartitionHandler) throws IOException {
        return new StepBuilder("readLoanCountMasterStep", jobRepository)
                .partitioner(readLoanCountStep.getName(), loanCountPartitioner(null))
                .partitionHandler(loanCountPartitionHandler)
                .build();
    }

    @Bean
    public Step readLoanCountStep(StockCsvFileReader stockCsvFileReader) {
        return new StepBuilder("readLoanCountStep", jobRepository)
                .<StockCsvData, StockCsvData>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(stockCsvFileReader)
                .writer(memoryIsbnWriter())
                .listener(stepLoggingListener)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler loanCountPartitionHandler(
            Step readLoanCountStep,
            TaskExecutor stockCsvTaskPool) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(readLoanCountStep);
        partitionHandler.setTaskExecutor(stockCsvTaskPool);
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


    // 새로 등록된 책을 메모리에 등록
    @Bean
    public ItemWriter<StockCsvData> memoryIsbnWriter() {
        return chunk -> chunk.getItems()
                .forEach(book -> {
                    int loanCount = book.getLoanCount();

                    if(!bookService.contains(book.getIsbn()))
                        throw new InvalidIsbn13Exception();

                    bookService.increase(book.getIsbn(), loanCount);
                });
    }
}
