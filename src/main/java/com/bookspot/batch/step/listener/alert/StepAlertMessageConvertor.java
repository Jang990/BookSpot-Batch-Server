package com.bookspot.batch.step.listener.alert;

import com.bookspot.batch.service.alert.AlertBody;
import com.bookspot.batch.service.alert.AlertMessage;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StepAlertMessageConvertor {
    private static final String TITLE_FORMAT = "[Step] stepExecutionId=%d stepName=%s 결과";
    private static final String TIME_RANGE_FORMAT = "%s ~ %s(현재 서버시간)";
    private static final String CONTENT_FORMAT = """
            Read Count : %d
            Write Count : %d
            Filter Count : %d
            Skip Count : %d
            """;

    private final TimeHolder timeHolder;

    public AlertMessage convert(StepExecution execution) {
        List<AlertBody> bodies = List.of(
                new AlertBody("상태", execution.getStatus().toString()),
                new AlertBody("동작 시간", toTimeRange(execution)),
                new AlertBody("내용", toContent(execution)),
                new AlertBody("실패 이유", getFailureMessages(execution))
        );

        return new AlertMessage(toTitle(execution), bodies);
    }

    private String toContent(StepExecution execution) {
        return CONTENT_FORMAT.formatted(
                execution.getReadCount(),
                execution.getWriteCount(),
                execution.getFilterCount(),
                execution.getSkipCount()
        );
    }

    private String toTimeRange(StepExecution execution) {
        return TIME_RANGE_FORMAT.formatted(
                execution.getStartTime().toString(),
                timeHolder.now()
        );
    }

    @NotNull
    private String toTitle(StepExecution execution) {
        return TITLE_FORMAT.formatted(
                execution.getId(),
                execution.getStepName()
        );
    }

    private String getFailureMessages(StepExecution execution) {
        return execution.getFailureExceptions().stream()
                .map(Throwable::getMessage)
                .collect(Collectors.joining("\n"));
    }

}
