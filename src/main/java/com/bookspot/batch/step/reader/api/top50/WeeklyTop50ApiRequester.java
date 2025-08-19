package com.bookspot.batch.step.reader.api.top50;


import com.bookspot.batch.global.openapi.ApiRequester;
import com.bookspot.batch.global.openapi.naru.NaruApiUrlCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WeeklyTop50ApiRequester {
    private final ApiRequester apiRequester;
    private final NaruApiUrlCreator naruApiUrlCreator;

    public List<WeeklyTop50ResponseSpec.Doc> findTop50(LocalDate baseDate) {
        String url = naruApiUrlCreator.buildWeeklyTop50Api(baseDate);

        return apiRequester.get(url, WeeklyTop50ResponseSpec.class).getResponse()
                .getDocs()
                .stream()
                .map(WeeklyTop50ResponseSpec.DocWrapper::getDoc)
                .toList();
    }

}
