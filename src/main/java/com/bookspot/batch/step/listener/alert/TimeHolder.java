package com.bookspot.batch.step.listener.alert;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TimeHolder {
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
