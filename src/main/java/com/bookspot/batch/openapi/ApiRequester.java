package com.bookspot.batch.openapi;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ApiRequester {
    private final WebClient client;

    public ApiRequester() {
        client = WebClient.builder()
                .defaultHeader("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
                .build();
    }

    public <T> T get(String url, Class<T> responseType) {
        return client.get()
                .uri(url)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }
}
