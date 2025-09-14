package com.bookspot.batch.job.temp.subjectCode;

import com.bookspot.batch.data.LibraryStock;
import com.bookspot.batch.global.config.TaskExecutorConfig;
import com.bookspot.batch.job.listener.alert.AlertJobListener;
import com.bookspot.batch.job.stock.StockSyncJobConfig;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.listener.alert.AlertStepListener;
import com.bookspot.batch.step.partition.StockCsvPartitionConfig;
import com.bookspot.batch.step.reader.CleansingStockFileReader;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 *  도서관 stock csv파일을 모두 다운오르받고
 *  db내의 모든 재고 정보의 SubjectCode를 csv 파일과 동기화함.
 */
@Deprecated
//@Configuration
@RequiredArgsConstructor
public class SubjectCodeForcedSyncJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager tm;

    @Bean
    public Job tempJob(
            AlertJobListener alertJobListener,
            Step stockCsvDownloadStep,
            Step stockCleansingMasterStep,
            Step duplicatedBookFilterMasterStep,
            Step tempStockUpdateMasterStep
    ) {
        return new JobBuilder("tempJob", jobRepository)
                .start(stockCsvDownloadStep)
                .next(stockCleansingMasterStep)
                .next(duplicatedBookFilterMasterStep)
                .next(tempStockUpdateMasterStep)
                .listener(alertJobListener)
                .build();
    }


    @Bean
    public Step tempStockUpdateMasterStep(
            Step tempStockStep,
            AlertStepListener alertStepListener,
            TaskExecutorPartitionHandler tempStockPartitionHandler) throws IOException {
        return new StepBuilder("tempStockMasterStep", jobRepository)
                .listener(alertStepListener)
                .partitioner(tempStockStep.getName(), tempStockPartitioner(null))
                .partitionHandler(tempStockPartitionHandler)
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler tempStockPartitionHandler(
            Step tempStockStep,
            TaskExecutor multiTaskPool) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(tempStockStep);
        partitionHandler.setTaskExecutor(multiTaskPool);
        partitionHandler.setGridSize(TaskExecutorConfig.MULTI_POOL_SIZE);
        return partitionHandler;
    }

    @Bean
    @StepScope
    public MultiResourcePartitioner tempStockPartitioner(
            @Value(StockSyncJobConfig.DUPLICATED_FILTER_DIR_PARAM) String root) throws IOException {
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
    public Step tempStockStep(
            CleansingStockFileReader cleansingStockFileReader,
            StepLoggingListener stepLoggingListener,
            JdbcBatchItemWriter<LibraryStock> tempStockUpdateWriter
    ) {
        return new StepBuilder("tempStockStep", jobRepository)
                .<LibraryStock, LibraryStock>chunk(StockSyncJobConfig.INSERT_CHUNK_SIZE, tm)
                .reader(cleansingStockFileReader)
                .processor(item -> {
                    if(item.getSubjectCode() == null || item.getSubjectCode().isBlank())
                        return null;
                    return item;
                })
                .writer(tempStockUpdateWriter)
                .listener(stepLoggingListener)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<LibraryStock> tempStockUpdateWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<LibraryStock>()
                .dataSource(dataSource)
                .sql("""
                UPDATE library_stock
                   SET subject_code = :subjectCode
                 WHERE library_id   = :libraryId
                   AND book_id      = :bookId
                """)
                .beanMapped()
                .build();
    }
}
