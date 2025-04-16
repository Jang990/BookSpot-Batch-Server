package com.bookspot.batch.step;

import com.bookspot.batch.global.file.stock.StockFileManager;
import com.bookspot.batch.job.stock.StockSyncJobConfig;
import com.bookspot.batch.step.processor.csv.stock.repository.LibraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@RequiredArgsConstructor
public class StockUpdatedAtStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final LibraryRepository libraryRepository;

    private final StockFileManager stockFileManager;

    @Bean
    public Step stockUpdatedAtStep() {
        return new StepBuilder("stockUpdatedAtStep", jobRepository)
                .tasklet(libraryStockUpdatedAtTasklet(null), platformTransactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet libraryStockUpdatedAtTasklet(@Value(StockSyncJobConfig.SOURCE_DIR_PARAM) String rootDirPath) {
        return (contribution, chunkContext) -> {
            stockFileManager.convertInnerFiles(rootDirPath)
                    .forEach(fileElement ->
                            // TODO: Bulk처리 필요
                            libraryRepository.updateStockDate(
                                    fileElement.libraryId(),
                                    fileElement.referenceDate()
                            )
                    );

            return RepeatStatus.FINISHED;
        };
    }
}
