package com.bookspot.batch.service.alert;

import java.util.List;
import java.util.Objects;

public record AlertMessage(String title, List<AlertBody> bodies) {

    public AlertMessage {
        Objects.requireNonNull(title);
        Objects.requireNonNull(bodies);
        if(bodies.isEmpty())
            throw new IllegalArgumentException("알림 메시지 바디는 필수 값");
    }
}
