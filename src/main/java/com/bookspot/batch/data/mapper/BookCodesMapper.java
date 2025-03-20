package com.bookspot.batch.data.mapper;

import com.bookspot.batch.data.BookCodes;
import com.bookspot.batch.global.crawler.kdc.KdcCode;
import org.springframework.stereotype.Service;

@Service
public class BookCodesMapper {
    public BookCodes transform(KdcCode kdcCode) {
        return new BookCodes(kdcCode.code(), kdcCode.name(), kdcCode.parentCode());
    }
}
