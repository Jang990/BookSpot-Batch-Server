package com.bookspot.batch.web;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class JobStatusService {
    private final JobExplorer jobExplorer;
    private final JobStatusRepository jobStatusRepository;

    public boolean isJobRunning(String jobName) {
        if(!jobExplorer.getJobNames().contains(jobName))
            throw new IllegalArgumentException("지원하지 않는 JobName");

        return !jobExplorer.findRunningJobExecutions(jobName).isEmpty();
    }

    public Set<String> findRunningJobName() {
        return jobStatusRepository.getRunningJobNames();
    }

    public boolean hasRunningJob() {
        return !jobStatusRepository.getRunningJobNames()
                .isEmpty();
    }
}
