package com.bookspot.batch.global.crawler.kdc;

import com.bookspot.batch.data.mapper.bookcode.ParentBookCodeResolver;
import org.springframework.stereotype.Service;

@Service
public class KdcParentBookCodeResolver implements ParentBookCodeResolver {
    @Override
    public Integer resolve(int code) {
        if(code % 100 == 0)
            return null;
        if(code % 10 == 0)
            return code / 100 * 100;
        return code / 10 * 10;
    }
}
