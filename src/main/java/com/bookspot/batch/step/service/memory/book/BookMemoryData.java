package com.bookspot.batch.step.service.memory.book;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookMemoryData {
    private String subjectCode;
    private int loanCount;
}
