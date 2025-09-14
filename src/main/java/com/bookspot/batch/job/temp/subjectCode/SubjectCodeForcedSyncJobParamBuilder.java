package com.bookspot.batch.job.temp.subjectCode;

import com.bookspot.batch.global.properties.files.BookSpotDirectoryProperties;
import com.bookspot.batch.global.properties.files.BookSpotFileProperties;
import com.bookspot.batch.job.BookSpotParentJobConfig;
import com.bookspot.batch.job.StockFileJobConfig;
import com.bookspot.batch.job.stock.StockSyncJobConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Deprecated
//@Component
@RequiredArgsConstructor
public class SubjectCodeForcedSyncJobParamBuilder {
    private final BookSpotDirectoryProperties directoryProperties;
    private final BookSpotFileProperties fileProperties;

    private JobParametersBuilder commonBuilder() {
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addLocalDate(
                BookSpotParentJobConfig.MONTH_PARAM_NAME,
                monthParam()
        );
//        builder.addString("Retry", "113");
//        builder.addLocalDateTime("Always_NEW_Start", LocalDateTime.now());
        return builder;
    }

    private LocalDate monthParam() {
        LocalDate now = LocalDate.now();
        return LocalDate.of(now.getYear(), now.getMonth(), 1);
    }

    public JobParameters buildTempParams() {
        return commonBuilder()
                .addString(
                        StockFileJobConfig.DOWNLOAD_DIR_PARAM_NAME,
                        directoryProperties.bookSync()
                )
                .addString(
                        StockSyncJobConfig.SOURCE_DIR_PARAM_NAME,
                        directoryProperties.bookSync()
                )
                .addString(
                        StockSyncJobConfig.CLEANSING_DIR_PARAM_NAME,
                        directoryProperties.cleansingStock()
                )
                .addString(
                        StockSyncJobConfig.DUPLICATED_FILTER_DIR_PARAM_NAME,
                        directoryProperties.filteredStock()
                )
                .toJobParameters();
    }
}
