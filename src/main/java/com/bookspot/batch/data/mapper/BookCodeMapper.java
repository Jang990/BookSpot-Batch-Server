package com.bookspot.batch.data.mapper;

import com.bookspot.batch.data.BookCode;
import com.bookspot.batch.global.crawler.kdc.KdcCode;
import org.springframework.stereotype.Service;

@Service
public class BookCodeMapper {
    public BookCode transform(KdcCode kdcCode) {
        return new BookCode(kdcCode.code(), kdcCode.name(), kdcCode.parentCode());
    }
}
