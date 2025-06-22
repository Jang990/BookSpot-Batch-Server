package com.bookspot.batch.job.listener.alert;

import com.bookspot.batch.service.alert.AlertBody;
import com.bookspot.batch.service.alert.AlertMessage;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobAlertMessageConvertorTest {
    JobAlertMessageConvertor convertor = new JobAlertMessageConvertor();
    @Test
    void 메시지_변환() {
        JobExecution execution = mock(JobExecution.class);

        when(execution.getJobId()).thenReturn(1L);
        JobInstance jobInstance = mock(JobInstance.class);
        when(execution.getJobInstance()).thenReturn(jobInstance);
        when(jobInstance.getJobName()).thenReturn("테스트 작업");

        when(execution.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(execution.getStartTime()).thenReturn(LocalDateTime.of(2025, 2, 5, 12, 30,32));
        when(execution.getEndTime()).thenReturn(LocalDateTime.of(2025, 2, 5, 14, 21,11));

        when(execution.getAllFailureExceptions()).thenReturn(
                List.of(
                        new IllegalStateException("몰라 일단 실패"),
                        new OutOfMemoryError("메모리 초과")
                )
        );

        AlertMessage result = convertor.convert(execution);
        assertEquals("[JOB] jobExecutionId=1 jobName=테스트 작업 결과", result.title());
        assertEquals(new AlertBody("상태", "COMPLETED"), result.bodies().get(0));
        assertEquals(new AlertBody("시작 시각", "2025-02-05T12:30:32"), result.bodies().get(1));
        assertEquals(new AlertBody("종료 시각", "2025-02-05T14:21:11"), result.bodies().get(2));
        assertEquals(new AlertBody("실패 이유", "몰라 일단 실패\n메모리 초과"), result.bodies().get(3));

    }

}