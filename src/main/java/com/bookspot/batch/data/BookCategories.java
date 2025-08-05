package com.bookspot.batch.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BookCategories {
    @JsonProperty("top_category")
    private final String topCategory;

    @JsonProperty("mid_category")
    private final String midCategory;

    @JsonProperty("leaf_category")
    private final String leafCategory;

    private BookCategories(
            String topCategory,
            String midCategory,
            String leafCategory
    ) {
        this.topCategory = topCategory;
        this.midCategory = midCategory;
        this.leafCategory = leafCategory;
    }

    public static BookCategories topCategory(String topCategory) {
        return new BookCategories(topCategory, null, null);
    }

    public static BookCategories midCategory(
            String topCategory,
            String midCategory
    ) {
        return new BookCategories(topCategory, midCategory, null);
    }

    public static BookCategories leafCategory(
            String topCategory,
            String midCategory,
            String leafCategory
    ) {
        return new BookCategories(topCategory, midCategory, leafCategory);
    }

}
