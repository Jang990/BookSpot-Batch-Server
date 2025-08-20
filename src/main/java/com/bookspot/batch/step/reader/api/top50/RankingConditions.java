package com.bookspot.batch.step.reader.api.top50;

import com.bookspot.batch.data.document.RankingAge;
import com.bookspot.batch.data.document.RankingGender;
import com.bookspot.batch.data.document.RankingType;

public record RankingConditions(
        RankingType periodType,
        RankingGender gender,
        RankingAge age
) {

}
