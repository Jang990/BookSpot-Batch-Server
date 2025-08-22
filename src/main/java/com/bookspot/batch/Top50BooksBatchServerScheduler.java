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

    /**
     * @see com.bookspot.batch.global.openapi.naru.NaruApiUrlCreator
     * referencdeDate로 다음 기간의 API를 만들어낸다.
     * Weekly: referencdeDate가 포함된 월~일요일
     * Monthly: referencdeDate가 포함된 1~마지막일
     * Daily: Weekly Monthly를 모두 검색
     */

//    @Scheduled(cron = "0 0 23 * * SUN", zone = "Asia/Seoul") // 매주 일요일 23:00
    public void fetchTop50Weekly() {
        LocalDate now = LocalDate.now();
        LocalDate referenceDate = localDateResolver.resolveMondayOfWeek(now);
        fetchTop50(referenceDate, RankingType.WEEKLY);
    }

//    @Scheduled(cron = "0 0 23 L * ?", zone = "Asia/Seoul") // 매월 마지막날 23:00
    public void fetchTop50Monthly() {
        LocalDate now = LocalDate.now();
        LocalDate referenceDate = localDateResolver.resolveFirstDayOfMonth(now);
        fetchTop50(referenceDate, RankingType.MONTHLY);
    }

    @Scheduled(cron = "0 0 22 * * TUE,WED,THU,FRI,SAT,SUN", zone = "Asia/Seoul") // 화~일요일(월요일 제외) 22:00
    public void fetchTop50Daily() {
        try {
            customJobLauncher.launchTop50BooksDailySync(LocalDate.now());
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
