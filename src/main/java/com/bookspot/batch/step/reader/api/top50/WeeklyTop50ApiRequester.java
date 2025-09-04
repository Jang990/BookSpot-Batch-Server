package com.bookspot.batch.step.reader.api.top50;


import com.bookspot.batch.data.Top50Book;
import com.bookspot.batch.data.Top50BookStrings;
import com.bookspot.batch.global.openapi.ApiRequester;
import com.bookspot.batch.global.openapi.naru.NaruApiUrlCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WeeklyTop50ApiRequester {
    private final ApiRequester apiRequester;
    private final NaruApiUrlCreator naruApiUrlCreator;

    public List<Top50BookStrings> findTop50(
            LocalDate baseDate,
            RankingConditions rankingConditions
    ) {
        String url = naruApiUrlCreator.buildWeeklyTop50Api(baseDate, rankingConditions);

        return apiRequester.get(url, WeeklyTop50ResponseSpec.class).getResponse()
                .getDocs()
                .stream()
                .map(WeeklyTop50ResponseSpec.DocWrapper::getDoc)
                .map(this::convert)
                .toList();
    }

    private Top50BookStrings convert(WeeklyTop50ResponseSpec.Doc response) {
        return new Top50BookStrings(
                response.getRanking(),
                response.getBookname(),
                response.getAuthors(),
                response.getPublisher(),
                response.getPublication_year(),
                response.getIsbn13(),
                response.getVol(),
                response.getClass_no(),
                response.getLoan_count()
        );
    }

}
