package com.bookspot.batch;

import com.bookspot.batch.data.document.RankingType;
import com.bookspot.batch.job.launcher.CustomJobLauncher;
import com.bookspot.batch.job.listener.alert.JobAlertMessageConvertor;
import com.bookspot.batch.service.alert.SlackAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @see com.bookspot.batch.global.config.TaskExecutorConfig
 * @see com.bookspot.batch.global.config.SchedulerConfig
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Top50BooksBatchServerScheduler {
    private final CustomJobLauncher customJobLauncher;
    private final SlackAlertService slackAlertService;
    private final JobAlertMessageConvertor convertor;

    @Scheduled(cron = "0 1 0 * * MON") // 매주 월요일 00:01
    public void fetchTop50Weekly() {
        fetchTop50(RankingType.WEEKLY);
    }

    @Scheduled(cron = "0 1 0 1 * ?") // 매월 1일 00:01
    public void fetchTop50Monthly() {
        fetchTop50(RankingType.MONTHLY);
    }

    private void fetchTop50(RankingType type) {
        LocalDate now = LocalDate.now();
        try {
            switch (type) {
                case WEEKLY -> customJobLauncher.launchTop50BooksOfWeek(now);
                case MONTHLY -> customJobLauncher.launchTop50BooksOfMonth(now);
            }
        } catch (Throwable t) {
            slackAlertService.error(
                    convertor.convertSimple(
                            "Top 50 책 스케줄러",
                            "책 불러오기 실패",
                            "진행중 예외 발생".concat(t.toString())
                    )
            );
            throw t;
        }
    }

}
