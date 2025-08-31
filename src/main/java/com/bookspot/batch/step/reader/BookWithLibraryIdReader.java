package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.BookCategories;
import com.bookspot.batch.data.LibraryIds;
import com.bookspot.batch.data.document.BookDocument;
import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.step.service.BookCodeResolver;
import com.bookspot.batch.step.service.LibraryStockRepository;
import jakarta.persistence.EntityManager;
import org.springframework.batch.item.*;

import java.util.*;
import java.util.stream.Collectors;

public class BookWithLibraryIdReader implements ItemReader<BookDocument>, ItemStream {

    protected static final String KEY_PAGE = "BookWithLibraryIdReader.currentPage";
    private static final String BOOK_SQL = """
            SELECT b FROM ConvertedUniqueBook b WHERE b.id > :lastId ORDER BY b.id ASC
            """;

    private final EntityManager entityManager;
    private final LibraryStockRepository libraryStockRepository;
    private final BookCodeResolver bookCodeResolver;
    private final int pageSize;

    private ExecutionContext executionContext;
    private List<BookDocument> currentBatch;
    private int currentIndex;
    private long currentPage;

    public BookWithLibraryIdReader(
            EntityManager entityManager,
            LibraryStockRepository libraryStockRepository,
            BookCodeResolver bookCodeResolver,
            int pageSize) {
        Objects.requireNonNull(entityManager);
        Objects.requireNonNull(libraryStockRepository);
        if(pageSize <= 0)
            throw new IllegalArgumentException();

        this.entityManager = entityManager;
        this.libraryStockRepository = libraryStockRepository;
        this.bookCodeResolver = bookCodeResolver;
        this.pageSize = pageSize;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
        if (executionContext.containsKey(KEY_PAGE)) {
            currentPage = executionContext.getLong(KEY_PAGE);
        } else {
            currentPage = 0;
        }
        saveState();
    }

    @Override
    public BookDocument read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(currentBatch == null
                || currentIndex >= currentBatch.size())
            fetchNextPage();
        if(isAllDataFetched())
            return null;
        return currentBatch.get(currentIndex++);
    }

    private void fetchNextPage() {
        saveState();
        List<ConvertedUniqueBook> content = queryBooks();
        if (content.isEmpty()) {
            currentBatch = Collections.emptyList();
            return;
        }

        List<LibraryIds> libraryIds = libraryStockRepository.findLibraryIds(content.stream()
                .mapToLong(ConvertedUniqueBook::getId)
                .boxed()
                .toList());
        currentBatch = aggregate(content, libraryIds);

        currentIndex = 0;
        currentPage = lastBookId(content);
    }

    private List<ConvertedUniqueBook> queryBooks() {
        return entityManager.createQuery(BOOK_SQL, ConvertedUniqueBook.class)
                .setParameter("lastId", currentPage)
                .setMaxResults(pageSize)
                .getResultList();
    }

    private long lastBookId(List<ConvertedUniqueBook> content) {
        return content.stream()
                .map(ConvertedUniqueBook::getId)
                .max(Long::compare)
                .orElseThrow(IllegalArgumentException::new);
    }

    private void saveState() {
        executionContext.putLong(KEY_PAGE, currentPage);
    }

    private List<BookDocument> aggregate(
            List<ConvertedUniqueBook> books,
            List<LibraryIds> libraryIds) {
        Map<Long, List<String>> libraryIdMap = libraryIds.stream()
                .collect(Collectors.toMap(LibraryIds::bookId, LibraryIds::libraryIds));

        return books.stream()
                .map(book -> new BookDocument(
                        book.getId().toString(),
                        book.getIsbn13(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublisher(),
                        book.getLoanCount(),
                        book.getMonthlyLoanIncrease(),
                        book.getSubjectCode(),
                        book.getPublicationYear() == null ? null : book.getPublicationYear().getValue(),
                        libraryIdMap.getOrDefault(book.getId(), new LinkedList<>()),
                        book.getSubjectCode() == null ?
                                BookCategories.EMPTY_CATEGORY
                                : bookCodeResolver.resolve(book.getSubjectCode()),
                        book.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    private boolean isAllDataFetched() {
        return isAfterLastElement() || isLastEmptyPage();
    }

    private boolean isLastEmptyPage() {
        return currentBatch != null && currentBatch.isEmpty();
    }

    private boolean isAfterLastElement() {
        return currentBatch != null
                && currentBatch.size() < pageSize
                && currentBatch.size() == currentIndex;
    }
}
