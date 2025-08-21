package com.bookspot.batch.step.book.api;

import com.bookspot.batch.data.BookCategories;
import com.bookspot.batch.data.Top50Book;
import com.bookspot.batch.data.document.BookRankingDocument;
import com.bookspot.batch.data.document.RankingAge;
import com.bookspot.batch.data.document.RankingGender;
import com.bookspot.batch.data.document.RankingType;
import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.infra.opensearch.BookRankingIndexSpec;
import com.bookspot.batch.infra.opensearch.OpenSearchRepository;
import com.bookspot.batch.step.reader.api.top50.RankingConditions;
import com.bookspot.batch.step.service.BookCodeResolver;
import com.bookspot.batch.step.service.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class Top50BookWriter implements ItemWriter<Top50Book> {
    private final LocalDate referenceDate;
    private final String rankingIndexName;
    private final RankingConditions rankingConditions;
    private final BookRepository bookRepository;
    private final OpenSearchRepository openSearchRepository;
    private final BookCodeResolver bookCodeResolver;

    @Override
    public void write(Chunk<? extends Top50Book> chunk) throws Exception {
        List<? extends Top50Book> top50Books = chunk.getItems();
        Map<String, ConvertedUniqueBook> entityMap = bookRepository.findByIsbn13In(isbn13List(top50Books)).stream()
                .collect(
                        Collectors.toMap(
                                ConvertedUniqueBook::getIsbn13,
                                book -> book
                        )
                );

        List<BookRankingDocument> rankingDocuments = new LinkedList<>();
        for (Top50Book top50Book : top50Books) {
            RankingResult rankingResult = new RankingResult(top50Book.ranking(), top50Book.loanCountInPeriod());
            if (entityMap.containsKey(top50Book.isbn13())) {
                rankingDocuments.add(
                        toDocument(
                                entityMap.get(top50Book.isbn13()),
                                rankingResult, referenceDate
                        )
                );
            } else {
                ConvertedUniqueBook entity = top50Book.toEntity();
                log.info(
                        "새로 등장한 책 정보 - ID={} Title={} ISBN13={}, ",
                        entity.getId(), entity.getTitle(), entity.getIsbn13()
                );
                rankingDocuments.add(
                        toDocument(
                                bookRepository.save(entity),
                                rankingResult, referenceDate
                        )
                );
            }
        }

        openSearchRepository.save(rankingIndexName, rankingDocuments);
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
                rankingResult.rank(), monday,
                rankingConditions.periodType(),
                rankingConditions.age(),
                rankingConditions.gender(),
                rankingResult.loanIncrease()
        );
    }

    private List<String> isbn13List(List<? extends Top50Book> top50Books) {
        return top50Books.stream()
                .map(Top50Book::isbn13)
                .toList();
    }
}
