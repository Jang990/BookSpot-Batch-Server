package com.bookspot.batch;

import com.bookspot.batch.job.launcher.CustomJobLauncher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TempRunner implements CommandLineRunner {
    // 임시 코드
    private final CustomJobLauncher customJobLauncher;

    @Override
    public void run(String... args) {
        customJobLauncher.launchBookSpotJob();
    }
}
