package com.bookspot.batch.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class JobStatusController {
    private final JobStatusService jobStatusService;

    @GetMapping("/job/{jobName}/is-running")
    public ResponseEntity<String> checkRunningJob(
            @PathVariable("jobName") String jobName
    ) {
        try {
            if(jobStatusService.isJobRunning(jobName))
                return ResponseEntity.ok("진행중");
            else
                return ResponseEntity.ok("진행중인 Job이 존재하지 않음");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/job/running/names")
    public ResponseEntity<String> checkRunningJob() {
        Set<String> result = jobStatusService.findRunningJobName();
        if(result.isEmpty())
            return ResponseEntity.ok("없음");
        else
            return ResponseEntity.ok(result.toString());
    }
}
