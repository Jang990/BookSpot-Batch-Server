package com.bookspot.batch.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class SimpleRequester {
    private static final RestTemplate restTemplate;

    static {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {

            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException { return false; }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {}
        });
    }

    public HttpStatusCode sendGetRequest(String url) {
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.GET, null, Void.class);
        return response.getStatusCode();
    }
}
