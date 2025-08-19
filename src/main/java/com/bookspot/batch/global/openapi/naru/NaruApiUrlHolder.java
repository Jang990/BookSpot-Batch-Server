package com.bookspot.batch.global.openapi.naru;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Getter
public class NaruApiUrlHolder {
    @Value("${api.naru.url.library}")
    private String libraryUrl;

    @Value("${api.naru.url.trend}")
    private String trendUrl;
}
