package com.bookspot.batch;

import com.bookspot.batch.job.launcher.CustomJobLauncher;
import com.bookspot.batch.job.listener.alert.JobAlertMessageConvertor;
import com.bookspot.batch.service.alert.SlackAlertService;
import com.bookspot.batch.web.JobStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * @see com.bookspot.batch.global.config.TaskExecutorConfig
 * @see com.bookspot.batch.global.config.SchedulerConfig
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchServerScheduler {
    private final JobStatusService jobStatusService;
    private final CustomJobLauncher customJobLauncher;
    private final SlackAlertService slackAlertService;
    private final JobAlertMessageConvertor convertor;

    @Value("${backend.url}")
    private String backendUrl;

    // 매월 새벽 1시 30분
    @Scheduled(cron = "0 30 1 2 * *", zone = "Asia/Seoul")
    public void scheduleBookSpotJob() {
        try {
            if (jobStatusService.hasRunningJob()) {
                log.warn("현재 진행중인 Job이 있어 실행하지 않습니다.");
                slackAlertService.error(
                        convertor.convertSimple(
                                "스케줄러 트리거 실패",
                                "BookSpot Batch 스케줄 트리거 실패",
                                "현재 진행중인 Job이 있어 실행하지 않습니다."
                        )
                );
                return;
            }

            customJobLauncher.launchBookSpotJob();
            slackAlertService.info(
                    convertor.convertSimple(
                            "스케줄러 트리거 성공",
                            "BookSpot Batch 스케줄 트리거 성공",
                            "트리거에 성공하고 작업이 진행중입니다."
                    )
            );
        } catch (Exception e) {
            log.warn("진행 중 예외 발생");
            slackAlertService.error(
                    convertor.convertSimple(
                            "스케줄러 트리거 실패",
                            "BookSpot Batch 스케줄 트리거 실패",
                            "진행 중 예외 발생 : ".concat(e.toString())
                    )
            );
        }
    }
}
