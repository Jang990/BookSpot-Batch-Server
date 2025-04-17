package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.IdRange;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;

public class IdRangeReader implements ItemStreamReader<IdRange> {
    public static final int FIXED_CHUNK_SIZE = 1;

    private final long minId, maxId, processedSize;
    private long nextId;

    public IdRangeReader(long minId, long maxId, long processedSize) {
        this.minId = minId;
        this.maxId = maxId;
        this.processedSize = processedSize;
    }

    @Override
    public void open(ExecutionContext ec) { nextId = minId; }

    @Override
    public IdRange read() {
        if (nextId > maxId) return null;
        long start = nextId;
        long end = Math.min(start + processedSize - 1, maxId);
        nextId = end + 1;
        return new IdRange(start, end);
    }
}
