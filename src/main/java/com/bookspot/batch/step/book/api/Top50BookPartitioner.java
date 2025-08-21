package com.bookspot.batch.step.book.api;

import com.bookspot.batch.data.document.RankingAge;
import com.bookspot.batch.data.document.RankingGender;
import com.bookspot.batch.data.document.RankingType;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Top50BookPartitioner implements Partitioner {
    private final LocalDate referenceDate;
    private final RankingType rankingType;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> map = new HashMap<>();

        int idx = 0;
        for (RankingGender gender : RankingGender.values()) {
            for (RankingAge age : RankingAge.values()) {
                ExecutionContext ctx = new ExecutionContext();
                ctx.putString("referenceDate", referenceDate.toString());
                ctx.putString("condPeriod", rankingType.toString());
                ctx.putString("condGender", gender.name());
                ctx.putString("condAge", age.name());

                map.put("partition-" + idx, ctx);
                idx++;
            }
        }
        return map;
    }
}
