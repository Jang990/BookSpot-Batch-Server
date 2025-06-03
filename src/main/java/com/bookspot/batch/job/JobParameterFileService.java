package com.bookspot.batch.job;

import com.bookspot.batch.job.loan.LoanAggregatedJobConfig;
import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class JobParameterFileService {
    public boolean exist(JobExecution lastJobExecution, String paramName) {
        String filePath = lastJobExecution.getJobParameters()
                .getString(paramName);

        return filePath != null && Files.exists(Path.of(filePath));
    }
}
