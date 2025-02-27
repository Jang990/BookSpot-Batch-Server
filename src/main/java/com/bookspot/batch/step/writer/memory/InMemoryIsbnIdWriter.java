package com.bookspot.batch.step.writer.memory;

import com.bookspot.batch.step.service.memory.bookid.Isbn13MemoryData;
import com.bookspot.batch.step.service.memory.bookid.IsbnMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class InMemoryIsbnIdWriter implements ItemWriter<Isbn13MemoryData> {
    private final IsbnMemoryRepository isbnEclipseMemoryRepository;

    @Override
    public void write(Chunk<? extends Isbn13MemoryData> chunk) throws Exception {
        chunk.getItems().forEach(isbnEclipseMemoryRepository::add);
    }
}
