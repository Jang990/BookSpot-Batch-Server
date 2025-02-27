package com.bookspot.batch.step.writer.memory;

import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class InMemoryIsbnWriter implements ItemWriter<String> {
    private final IsbnSet isbnSet;

    @Override
    public void write(Chunk<? extends String> chunk) throws Exception {
        chunk.getItems().forEach(isbnSet::add);
    }
}
