package com.bookspot.batch.global.crawler.kdc.css;

public class CssTarget {
    private final int MIN_TARGET;
    private final int MAX_TARGET;
    private final int added;

    private int value;

    protected static CssTarget top() {
        return new CssTarget(12, 39, 3);
    }

    protected static CssTarget mid() {
        return new CssTarget(1, 5, 1);
    }

    protected static CssTarget midLine() {
        return new CssTarget(2, 4, 2);
    }

    protected static CssTarget leaf() {
        return new CssTarget(1, 9, 1);
    }

    public CssTarget(int min, int max, int added) {
        this.MIN_TARGET = min;
        this.MAX_TARGET = max;
        this.value = min;
        this.added = added;
    }

    public int value() {
        return value;
    }

    public boolean hasNext() {
        return value + added <= MAX_TARGET;
    }

    public void next() {
        if(!hasNext())
            throw new OutOfRangeCssTargetException();

        value += added;
    }

    public void first() {
        value = MIN_TARGET;
    }
}
