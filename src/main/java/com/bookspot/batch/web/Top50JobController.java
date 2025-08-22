package com.bookspot.batch.web;

import com.bookspot.batch.job.launcher.CustomJobLauncher;
import com.bookspot.batch.job.launcher.LocalDateHolder;
import com.bookspot.batch.job.launcher.Top50BooksLauncher;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

@RestController
@RequiredArgsConstructor
public class Top50JobController {
    private final JobStatusService jobStatusService;
    private final Top50BooksLauncher top50BooksLauncher;
    private final LocalDateHolder localDateHolder;

    @PostMapping("/job/bookOpenSearch/top50/monthly")
    public ResponseEntity<String> startMonthlyTop50Job(
            @RequestBody DateDto dateDto
    ) {
        if(jobStatusService.hasRunningJob())
            return ResponseEntity.badRequest().body("현재 진행중인 Job이 있습니다.");

        LocalDate now = localDateHolder.now();
        LocalDate referenceDate = LocalDate.parse(dateDto.getReferenceDateString());
        if(referenceDate.getMonth().equals(now.getMonth()))
            return ResponseEntity.badRequest().body("집계가 끝나지 않은 날의 데이터를 가져올 수 없습니다.");

        if (now.getMonthValue() - referenceDate.getMonthValue() >= 2)
            return ResponseEntity.badRequest().body("기간이 2개월 이상이면 허용되지 않습니다.");
        
        top50BooksLauncher.launchAllMonthly(referenceDate);
        return ResponseEntity.ok("Job을 진행함");
    }


    @PostMapping("/job/bookOpenSearch/top50/weekly")
    public ResponseEntity<String> startWeeklyTop50Job(
            @RequestBody DateDto dateDto
    ) {
        if(jobStatusService.hasRunningJob())
            return ResponseEntity.badRequest().body("현재 진행중인 Job이 있습니다.");

        LocalDate now = localDateHolder.now();
        LocalDate referenceDate = LocalDate.parse(dateDto.getReferenceDateString());
        if (!referenceDate.isBefore(getMondayOfThisWeek(now)))
            return ResponseEntity.badRequest().body("집계가 끝나지 않은 날의 데이터를 가져올 수 없습니다.");

        long weeksBetween = ChronoUnit.WEEKS.between(referenceDate, now);
        if (weeksBetween >= 5)
            return ResponseEntity.badRequest().body("기간이 5주 이상이면 허용되지 않습니다.");

        top50BooksLauncher.launchAllWeekly(referenceDate);
        return ResponseEntity.ok("Job을 진행함");
    }

    private LocalDate getMondayOfThisWeek(LocalDate referenceDate) {
        return referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    @Data
    public static class DateDto {
        private String referenceDateString;
    }

}
