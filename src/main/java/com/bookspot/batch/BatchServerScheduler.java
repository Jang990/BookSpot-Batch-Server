package com.bookspot.batch;

import com.bookspot.batch.data.document.RankingAge;
import com.bookspot.batch.data.document.RankingGender;
import com.bookspot.batch.data.document.RankingType;
import com.bookspot.batch.job.launcher.CustomJobLauncher;
import com.bookspot.batch.job.listener.alert.JobAlertMessageConvertor;
import com.bookspot.batch.service.SimpleRequester;
import com.bookspot.batch.service.alert.SlackAlertService;
import com.bookspot.batch.step.reader.api.top50.RankingConditions;
import com.bookspot.batch.web.JobStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDate;
import java.util.List;

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
    private final SimpleRequester simpleRequester;

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

    @Scheduled(cron = "0 */15 * * * *", zone = "Asia/Seoul")
    public void warmUpBackend() {
        List<String> urls = List.of(
                backendUrl.concat("/api/books?"),
                backendUrl.concat("/api/books?title=한강"),
                backendUrl.concat("/api/books?title=돼지"),
                backendUrl.concat("/api/books?libraryId=682"),
                backendUrl.concat("/api/books?title=객체&categoryId=5&categoryLevel=LEAF")
        );

        int failed = 0;
        for (String url : urls) {
            try {
                HttpStatusCode status = simpleRequester.sendGetRequest(url);
                if (!status.is2xxSuccessful())
                    failed++;
            } catch (ResourceAccessException e) {
                failed++;
            }
        }

        if (failed >= 3) {
            slackAlertService.error(
                    convertor.convertSimple(
                            "Backend 웜업 스케줄러",
                            "Backend 문제 발생",
                            "%d개의 요청 중 %d개의 요청 실패".formatted(urls.size(), failed)
                    )
            );
            log.error("Backend 웜업 작업 실패. - {}개의 요청 중 {}개의 요청 실패", urls.size(), failed);
            return;
        }

        log.info("Backend 웜업 작업 완료. - {}개의 요청 중 {}개의 요청 실패", urls.size(), failed);
    }

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
            for (RankingGender gender : RankingGender.values()) {
                for (RankingAge age : RankingAge.values()) {
                    switch (type) {
                        case WEEKLY -> {
                            customJobLauncher.launchTop50BooksOfWeek(now, gender, age);
                            log.info("성별: {}, 나이: {} 주간 인기도서 top 50 Job 완료", gender, age);
                        }
                        case MONTHLY -> {
                            customJobLauncher.launchTop50BooksOfMonth(now, gender, age);
                            log.info("성별: {}, 나이: {} 월별 인기도서 top 50 Job 완료", gender, age);
                        }
                    }
                }
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
