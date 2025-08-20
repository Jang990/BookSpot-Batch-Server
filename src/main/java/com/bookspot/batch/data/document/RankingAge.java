package com.bookspot.batch.data.document;

import lombok.Getter;

@Getter
public enum RankingAge {
    AGE_0_14(0, 14),
    AGE_15_19(15, 19),
    AGE_20_29(20,29),
    AGE_30_39(30, 39),
    AGE_40_49(40, 49),
    AGE_50_UP(50, null),
    ALL(null, null);

    private final Integer start, end;

    RankingAge(Integer start, Integer end) {
        this.start = start;
        this.end = end;
    }
}
