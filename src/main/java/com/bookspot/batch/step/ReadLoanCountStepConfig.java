package com.bookspot.batch.step;

import com.bookspot.batch.data.file.csv.StockCsvData;
import com.bookspot.batch.step.listener.StepLoggingListener;
import com.bookspot.batch.step.processor.exception.InvalidIsbn13Exception;
import com.bookspot.batch.step.reader.MultiStockCsvFileReader;
import com.bookspot.batch.step.service.memory.loan.InMemoryLoanCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ReadLoanCountStepConfig {
    private static final int BOOK_SYNC_CHUNK_SIZE = 5_000;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final InMemoryLoanCountService bookService;
    private final StepLoggingListener stepLoggingListener;

    @Bean
    public Step readLoanCountStep(
            MultiStockCsvFileReader multiBookStockCsvFileReader) {
        return new StepBuilder("readLoanCountStep", jobRepository)
                .<StockCsvData, StockCsvData>chunk(BOOK_SYNC_CHUNK_SIZE, platformTransactionManager)
                .reader(multiBookStockCsvFileReader)
                .writer(memoryIsbnWriter())
                .listener(stepLoggingListener)
                .build();
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
