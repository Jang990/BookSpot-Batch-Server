package com.bookspot.batch.step.processor.csv;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TextEllipsiserTest {
    TextEllipsiser textEllipsiser = new TextEllipsiser();
    @Test
    void 글자가_제한_길이를_넘으면_자르고_점을_남김() {
        String AAAAAA = "A".repeat(6);

        Assertions.assertEquals(
                "AA...",
                textEllipsiser.ellipsize(AAAAAA,  5)
        );
    }

}