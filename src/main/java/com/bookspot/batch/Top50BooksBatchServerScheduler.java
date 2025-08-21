package com.bookspot.batch;

import com.bookspot.batch.data.document.RankingType;
import com.bookspot.batch.job.launcher.CustomJobLauncher;
import com.bookspot.batch.job.launcher.LocalDateResolver;
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

    private final LocalDateResolver localDateResolver;

    @Scheduled(cron = "0 1 0 * * MON") // 매주 월요일 00:01
    public void fetchTop50Weekly() {
        LocalDate now = LocalDate.now();
        LocalDate referenceDate = localDateResolver.resolveMondayOfLastWeek(now);
        fetchTop50(referenceDate, RankingType.WEEKLY);
    }

    @Scheduled(cron = "0 1 0 1 * ?") // 매월 1일 00:01
    public void fetchTop50Monthly() {
        LocalDate now = LocalDate.now();
        LocalDate referenceDate = localDateResolver.resolveFirstDayOfLastMonth(now);
        fetchTop50(referenceDate, RankingType.MONTHLY);
    }

    private void fetchTop50(LocalDate referenceDate, RankingType type) {
        try {
            switch (type) {
                case WEEKLY -> customJobLauncher.launchTop50BooksOfWeek(referenceDate);
                case MONTHLY -> customJobLauncher.launchTop50BooksOfMonth(referenceDate);
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
