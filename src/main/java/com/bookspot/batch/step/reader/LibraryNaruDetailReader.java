package com.bookspot.batch.step.reader;

import com.bookspot.batch.step.LibraryStepConst;
import com.bookspot.batch.data.crawler.LibraryNaruDetail;
import com.bookspot.batch.step.reader.crawler.library.NaruDetailParser;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryNaruDetailReader implements ItemReader<LibraryNaruDetail> {
    private static final int LIBRARY_REQUEST_LIMIT = 2000;

    private final NaruDetailParser naruDetailParser;

    private int currentPage;
    private int currentIndex;
    private List<LibraryNaruDetail> currentBatch;

    @PostConstruct
    protected void postConstruct() {
        currentPage = 1;
        currentIndex = 0;
        currentBatch = new ArrayList<>();
    }

    @Override
    public LibraryNaruDetail read() throws Exception {
        if(isAllDataFetched())
            return null;
        if(currentIndex >= currentBatch.size())
            fetchNextPage();
        return currentBatch.get(currentIndex++);
    }

    private void fetchNextPage() {
        currentBatch = naruDetailParser.parseDetail(
                PageRequest.of(currentPage, LibraryStepConst.LIBRARY_CHUNK_SIZE));
        currentIndex = 0;
        currentPage++;
    }

    public boolean isAllDataFetched() {
        if (getFetchedDataCount() >= LIBRARY_REQUEST_LIMIT) {
            log.warn("도서관 파싱 도중 요청 한계를 초과함 {}개 파싱", getFetchedDataCount());
            return true;
        }

        return currentPage != 1
                && currentBatch.size() < LibraryStepConst.LIBRARY_CHUNK_SIZE
                    && currentBatch.size() == currentIndex;
    }

    private int getFetchedDataCount() {
        return (currentPage - 1) * LibraryStepConst.LIBRARY_CHUNK_SIZE;
    }
}
