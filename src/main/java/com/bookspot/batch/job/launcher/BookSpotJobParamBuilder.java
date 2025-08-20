package com.bookspot.batch.job.launcher;

import com.bookspot.batch.global.properties.files.BookSpotDirectoryProperties;
import com.bookspot.batch.global.properties.files.BookSpotFileProperties;
import com.bookspot.batch.job.BookSpotParentJobConfig;
import com.bookspot.batch.job.Top50BooksJobConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BookSpotJobParamBuilder {
    private final BookSpotDirectoryProperties directoryProperties;
    private final BookSpotFileProperties fileProperties;

    private JobParametersBuilder commonBuilder() {
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addLocalDate(
                BookSpotParentJobConfig.MONTH_PARAM_NAME,
                monthParam()
        );
        return builder;
    }

    private LocalDate monthParam() {
        LocalDate now = LocalDate.now();
        return LocalDate.of(now.getYear(), now.getMonth(), 1);
    }

    public JobParameters buildBookSpotParams() {
        return commonBuilder()
                .addString(
                        BookSpotParentJobConfig.LIBRARY_FILE_PARAM_NAME,
                        fileProperties.library()
                )
                .addString(
                        BookSpotParentJobConfig.STOCK_DIR_PARAM_NAME,
                        directoryProperties.bookSync()
                )
                .addString(
                        BookSpotParentJobConfig.DOWNLOAD_DIR_PARAM_NAME,
                        directoryProperties.bookSync()
                )
                .addString(
                        BookSpotParentJobConfig.LOAN_OUTPUT_FILE_PARAM_NAME,
                        fileProperties.loan()
                )
                .addString(
                        BookSpotParentJobConfig.CLEANSING_DIR_PARAM_NAME,
                        directoryProperties.cleansingStock()
                )
                .addString(
                        BookSpotParentJobConfig.DUPLICATED_FILTER_DIR_PARAM_NAME,
                        directoryProperties.filteredStock()
                )
                .addString(
                        BookSpotParentJobConfig.DELETE_DIR_PARAM_NAME,
                        directoryProperties.deletedStock()
                )
                .toJobParameters();
    }

    public JobParameters buildOpenSearchParams() {
        return commonBuilder().toJobParameters();
    }

    public JobParameters buildTop50BooksJobParams(LocalDate referenceDate) {
        return new JobParametersBuilder()
                .addLocalDate(
                        Top50BooksJobConfig.REFERENCE_DATE_PARAM_NAME,
                        referenceDate
                )
                .toJobParameters();
    }
}
