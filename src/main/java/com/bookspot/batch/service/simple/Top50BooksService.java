package com.bookspot.batch.service.simple;

import com.bookspot.batch.data.BookCategories;
import com.bookspot.batch.data.document.BookRankingDocument;
import com.bookspot.batch.data.document.RankingAge;
import com.bookspot.batch.data.document.RankingGender;
import com.bookspot.batch.data.document.RankingType;
import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.infra.opensearch.BookRankingIndexSpec;
import com.bookspot.batch.infra.opensearch.OpenSearchRepository;
import com.bookspot.batch.step.reader.api.top50.WeeklyTop50ApiRequester;
import com.bookspot.batch.step.reader.api.top50.WeeklyTop50ResponseSpec;
import com.bookspot.batch.step.service.BookCodeResolver;
import com.bookspot.batch.step.service.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class Top50BooksService {
    private final WeeklyTop50ApiRequester top50ApiRequester;
    private final BookRepository bookRepository;
    private final OpenSearchRepository openSearchRepository;
    private final BookCodeResolver bookCodeResolver;

    public void updateTop50Books(LocalDate monday) {
        List<WeeklyTop50ResponseSpec.Doc> top50Books = top50ApiRequester.findTop50(monday);

        Map<String, ConvertedUniqueBook> entityMap = bookRepository.findByIsbn13In(isbn13List(top50Books)).stream()
                .collect(
                        Collectors.toMap(
                                ConvertedUniqueBook::getIsbn13,
                                book -> book
                        )
                );

        List<BookRankingDocument> rankingDocuments = new LinkedList<>();
        for (WeeklyTop50ResponseSpec.Doc top50Book : top50Books) {
            RankingResult rankingResult = new RankingResult(top50Book.getRanking(), top50Book.getLoan_count());
            if (entityMap.containsKey(top50Book.getIsbn13())) {
                rankingDocuments.add(
                        toDocument(
                                entityMap.get(top50Book.getIsbn13()),
                                rankingResult, monday
                        )
                );
            } else {
                ConvertedUniqueBook entity = toEntity(top50Book);
                log.info(
                        "새로 등장한 책 정보 - ID={} Title={} ISBN13={}, ",
                        entity.getId(), entity.getTitle(), entity.getIsbn13()
                );
                rankingDocuments.add(
                        toDocument(
                                bookRepository.save(entity),
                                rankingResult, monday
                        )
                );
            }
        }

        openSearchRepository.save(new BookRankingIndexSpec().serviceIndexName(), rankingDocuments);

    }

    private ConvertedUniqueBook toEntity(WeeklyTop50ResponseSpec.Doc top50Book) {
        return new ConvertedUniqueBook(
                top50Book.getIsbn13(),
                top50Book.getBookname(),
                top50Book.getAuthors(),
                top50Book.getPublisher(),
                top50Book.getVol(),
                top50Book.getLoan_count(),
                (int) top50Book.getClass_no(),
                Year.of(top50Book.getPublication_year())
        );
    }

    private BookRankingDocument toDocument(ConvertedUniqueBook book, RankingResult rankingResult, LocalDate monday) {
        return new BookRankingDocument(
                book.getId().toString(),
                book.getIsbn13(), book.getTitle(), book.getAuthor(), book.getPublisher(),
                book.getPublicationYear() == null ? null : book.getPublicationYear().getValue(),
                book.getCreatedAt() == null ? monday : book.getCreatedAt(),
                book.getSubjectCode(),
                book.getSubjectCode() == null ?
                        BookCategories.EMPTY_CATEGORY
                        : bookCodeResolver.resolve(book.getSubjectCode()),
                rankingResult.rank(), monday, RankingType.WEEKLY,
                RankingAge.ALL, RankingGender.ALL, rankingResult.loanIncrease()
        );
    }

    private List<String> isbn13List(List<WeeklyTop50ResponseSpec.Doc> top50Books) {
        return top50Books.stream()
                .map(WeeklyTop50ResponseSpec.Doc::getIsbn13)
                .toList();
    }
}
