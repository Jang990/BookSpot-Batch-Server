package com.bookspot.batch.step.reader;

import com.bookspot.batch.data.LibraryIds;
import com.bookspot.batch.data.TEMP_BookDocument;
import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.step.service.LibraryStockRepository;
import com.bookspot.batch.step.service.UniqueBookRepository;
import org.springframework.batch.item.*;
import org.springframework.data.domain.PageRequest;

import java.util.*;
import java.util.stream.Collectors;

public class BookWithLibraryIdReader implements ItemReader<TEMP_BookDocument>, ItemStream {

    private static final String KEY_PAGE = "BookWithLibraryIdReader.currentPage";
    private static final String KEY_INDEX = "BookWithLibraryIdReader.currentIndex";

    private final UniqueBookRepository bookRepository;
    private final LibraryStockRepository libraryStockRepository;
    private final int pageSize;

    private ExecutionContext executionContext;
    private List<TEMP_BookDocument> currentBatch;
    private int currentIndex;
    private int currentPage;

    public BookWithLibraryIdReader(
            UniqueBookRepository bookRepository,
            LibraryStockRepository libraryStockRepository,
            int pageSize) {
        Objects.requireNonNull(bookRepository);
        Objects.requireNonNull(libraryStockRepository);
        if(pageSize <= 0)
            throw new IllegalArgumentException();

        this.bookRepository = bookRepository;
        this.libraryStockRepository = libraryStockRepository;
        this.pageSize = pageSize;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        ItemStream.super.open(executionContext);
        this.executionContext = executionContext;
        if (executionContext.containsKey(KEY_PAGE)) {
            currentPage = executionContext.getInt(KEY_PAGE);
            currentIndex = executionContext.getInt(KEY_INDEX);
        } else {
            currentPage = 0;
            currentIndex = 0;
        }
        currentBatch = new ArrayList<>(pageSize);
    }

    @Override
    public TEMP_BookDocument read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(currentIndex >= currentBatch.size())
            fetchNextPage();
        if(isAllDataFetched())
            return null;
        return currentBatch.get(currentIndex++);
    }

    private void fetchNextPage() {
        List<ConvertedUniqueBook> content = bookRepository.findAll(PageRequest.of(currentPage, pageSize)).getContent();

        List<LibraryIds> libraryIds = libraryStockRepository.findLibraryIds(content.stream()
                .mapToLong(ConvertedUniqueBook::getId)
                .boxed()
                .toList());
        currentBatch = aggregate(content, libraryIds);

        currentIndex = 0;
        currentPage++;
        saveState();
    }

    private void saveState() {
        executionContext.putInt(KEY_PAGE, currentPage);
        executionContext.putInt(KEY_INDEX, currentIndex);
    }

    private List<TEMP_BookDocument> aggregate(
            List<ConvertedUniqueBook> books,
            List<LibraryIds> libraryIds) {
        Map<Long, List<String>> libraryIdMap = libraryIds.stream()
                .collect(Collectors.toMap(LibraryIds::bookId, LibraryIds::libraryIds));

        return books.stream()
                .map(book -> new TEMP_BookDocument(
                        book.getId().toString(),
                        book.getIsbn13(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublisher(),
                        book.getLoanCount(),
                        book.getSubjectCode(),
                        book.getPublicationYear() == null ? null : book.getPublicationYear().getValue(),
                        libraryIdMap.getOrDefault(book.getId(), new LinkedList<>())
                ))
                .collect(Collectors.toList());
    }

    private boolean isAllDataFetched() {
        return currentPage != 0
                && currentBatch.size() < pageSize
                && currentBatch.size() == currentIndex;
    }
}
