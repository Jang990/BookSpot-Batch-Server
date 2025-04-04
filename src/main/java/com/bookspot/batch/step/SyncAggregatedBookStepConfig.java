package com.bookspot.batch.step;

import com.bookspot.batch.data.file.csv.AggregatedBook;
import com.bookspot.batch.global.file.spec.AggregatedBooksCsvSpec;
import com.bookspot.batch.job.validator.FilePathJobParameterValidator;
import com.bookspot.batch.step.listener.StepLoggingListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SyncAggregatedBookStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final StepLoggingListener stepLoggingListener;

    private static final int CHUNK_SIZE = 5_000;

    @Bean
    public Step syncAggregatedBookStep() {
        return new StepBuilder("syncAggregatedBookStep", jobRepository)
                .<AggregatedBook, AggregatedBook>chunk(CHUNK_SIZE, transactionManager)
                .reader(aggregatedBookCsvFileReader(null))
                .writer(stockBookWriter())
                .listener(stepLoggingListener)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<AggregatedBook> aggregatedBookCsvFileReader(
            @Value(FilePathJobParameterValidator.AGGREGATED_FILE_PATH) String aggregatedFilePath) {
        return new FlatFileItemReaderBuilder<AggregatedBook>()
                .name("aggregatedBookCsvFileReader")
                .encoding("UTF-8")
                .resource(new FileSystemResource(aggregatedFilePath))
                .delimited()
                .names(
                        Arrays.stream(AggregatedBooksCsvSpec.values())
                                .map(AggregatedBooksCsvSpec::value)
                                .toArray(String[]::new)
                )
                .fieldSetMapper(fieldSet ->
                     new AggregatedBook(
                             fieldSet.readString(AggregatedBooksCsvSpec.ISBN13.value()),
                             fieldSet.readInt(AggregatedBooksCsvSpec.LOAN_COUNT.value())
                     )
                )
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<AggregatedBook> stockBookWriter() {
        return new JdbcBatchItemWriterBuilder<AggregatedBook>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO book
                        (isbn13, loan_count)
                        VALUES(?, ?)
                        ON DUPLICATE KEY UPDATE
                            loan_count = VALUES(loan_count)
                        """)
//                monthlyLoanIncrease = loan_count + VALUES(loan_count);
                .itemPreparedStatementSetter(
                        (book, ps) -> {
                            ps.setString(1, book.isbn13());
                            ps.setInt(2, book.loanCount());
                        })
                .assertUpdates(false)
                .build();
    }
}
