package com.bookspot.batch.library;

import com.bookspot.batch.library.api.LibraryApiRequester;
import com.bookspot.batch.library.data.Library;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LibraryItemReader implements ItemReader<Library> {

    private final LibraryApiRequester libraryApiRequester;

    private int totalLibraries;
    private int currentPage;
    private int currentIndex;
    private List<Library> currentBatch;

    @PostConstruct
    protected void postConstruct() {
        totalLibraries = libraryApiRequester.countSupportedLibrary();
        currentPage = 1;
        currentIndex = 0;
        currentBatch = new ArrayList<>();
    }

    @Override
    public Library read() throws Exception {
        if(isAllDataFetched())
            return null;
        if(currentIndex >= currentBatch.size())
            fetchNextPage();
        return currentBatch.get(currentIndex++);
    }

    private void fetchNextPage() {
        currentBatch = libraryApiRequester.findAllSupportedLibrary(
                PageRequest.of(currentPage, LibraryJobConst.LIBRARY_CHUNK_SIZE));
        currentIndex = 0;
        currentPage++;
    }

    public boolean isAllDataFetched() {
        return getFetchedDataCount() >= totalLibraries
                && currentBatch.size() == currentIndex;
    }

    private int getFetchedDataCount() {
        return (currentPage - 1) * LibraryJobConst.LIBRARY_CHUNK_SIZE;
    }
}
