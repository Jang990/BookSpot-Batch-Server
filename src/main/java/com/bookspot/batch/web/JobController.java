package com.bookspot.batch.web;

import com.bookspot.batch.job.launcher.CustomJobLauncher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JobController {
    private final CustomJobLauncher customJobLauncher;
    private final JobStatusService jobStatusService;

    @PostMapping("/job/bookSpot/start")
    public ResponseEntity<String> startJob() {
        if(jobStatusService.hasRunningJob())
            return ResponseEntity.badRequest().body("현재 진행중인 Job이 있습니다.");
        customJobLauncher.launchBookSpotJob();
        return ResponseEntity.ok("Job을 진행함");
    }

    @PostMapping("/job/bookOpenSearch/start")
    public ResponseEntity<String> startOpenSearchJob() {
        if(jobStatusService.hasRunningJob())
            return ResponseEntity.badRequest().body("현재 진행중인 Job이 있습니다.");
        customJobLauncher.launchBookOpenSearchSyncJob();
        return ResponseEntity.ok("Job을 진행함");
    }

}
