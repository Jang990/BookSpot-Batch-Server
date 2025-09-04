package com.bookspot.batch.step.book.api;

import com.bookspot.batch.data.Top50Book;
import com.bookspot.batch.data.Top50BookStrings;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class Top50BookApiProcessor implements ItemProcessor<Top50BookStrings, Top50Book> {
    @Override
    public Top50Book process(Top50BookStrings item) throws Exception {
        return new Top50Book(
                Integer.parseInt(item.ranking()),
                item.title().trim(),
                item.authors().trim(),
                item.publisher().trim(),
                Year.of(Integer.parseInt(item.publicationYear())),
                item.isbn13().trim(),
                item.vol(),
                Double.parseDouble(item.subjectCode()),
                Integer.parseInt(item.loanCount())
        );
    }
}
