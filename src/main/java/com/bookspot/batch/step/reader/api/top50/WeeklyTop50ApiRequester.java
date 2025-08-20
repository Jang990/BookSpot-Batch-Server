package com.bookspot.batch.step.reader.api.top50;


import com.bookspot.batch.data.Top50Book;
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

    public List<Top50Book> findTop50(LocalDate baseDate) {
        String url = naruApiUrlCreator.buildWeeklyTop50Api(baseDate);

        return apiRequester.get(url, WeeklyTop50ResponseSpec.class).getResponse()
                .getDocs()
                .stream()
                .map(WeeklyTop50ResponseSpec.DocWrapper::getDoc)
                .map(this::convert)
                .toList();
    }

    private Top50Book convert(WeeklyTop50ResponseSpec.Doc response) {
        return new Top50Book(
                response.getRanking(),
                response.getBookname().trim(),
                response.getAuthors().trim(),
                response.getPublisher().trim(),
                Year.of(response.getPublication_year()),
                response.getIsbn13().trim(),
                response.getVol(),
                response.getClass_no(),
                response.getLoan_count()
        );
    }

}
