package com.bookspot.batch.step.writer.memory;

import com.bookspot.batch.data.file.csv.ConvertedUniqueBook;
import com.bookspot.batch.step.service.memory.isbn.IsbnSet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class InMemoryIsbnWriterWithCsv implements ItemWriter<ConvertedUniqueBook> {
    private final IsbnSet isbnSet;

    @Override
    public void write(Chunk<? extends ConvertedUniqueBook> chunk) throws Exception {
        chunk.getItems().stream()
                .map(ConvertedUniqueBook::getIsbn13)
                .forEach(isbnSet::add);
    }
}
