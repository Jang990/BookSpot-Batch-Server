package com.bookspot.batch.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookCode {
    @Id
    private Integer id;
    private String name;

    public BookCode(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
