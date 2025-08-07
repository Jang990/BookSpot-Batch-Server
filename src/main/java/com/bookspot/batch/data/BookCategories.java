package com.bookspot.batch.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class BookCategories {
    public static final BookCategories EMPTY_CATEGORY = new BookCategories(null, null, null);

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
        return new BookCategories(topCategory, topCategory, topCategory);
    }

    public static BookCategories midCategory(
            String topCategory,
            String midCategory
    ) {
        return new BookCategories(topCategory, midCategory, midCategory);
    }

    public static BookCategories leafCategory(
            String topCategory,
            String midCategory,
            String leafCategory
    ) {
        return new BookCategories(topCategory, midCategory, leafCategory);
    }

}
