package com.bookspot.batch.global.crawler.kdc.css;

public class KdcCssTarget {
    private CssTarget top = CssTarget.top();
    private CssTarget midLine = CssTarget.midLine();
    private CssTarget mid = CssTarget.mid();
    private CssTarget leaf = CssTarget.leaf();

    public boolean hasNextTop() {
        return top.hasNext();
    }

    public boolean hasNextMid() {
        return mid.hasNext() || midLine.hasNext();
    }

    public boolean hasNextLeaf() {
        return leaf.hasNext();
    }

    public void nextTop() {
        if(!hasNextTop())
            throw new OutOfRangeCssTargetException();
        top.next();
        mid.first();
        midLine.first();
        leaf.first();
    }

    public void nextMid() {
        if(!hasNextMid())
            throw new OutOfRangeCssTargetException();

        if (!mid.hasNext() && midLine.hasNext()) {
            mid.first();
            midLine.next();
            leaf.first();
            return;
        }

        mid.next();
        leaf.first();
    }

    public void nextLeaf() {
        if(!hasNextLeaf())
            throw new OutOfRangeCssTargetException();
        leaf.next();
    }

    public int top() {
        return top.value();
    }

    public int midLine() {
        return midLine.value();
    }

    public int mid() {
        return mid.value();
    }

    public int leafLine() {
        return midLine.value() + 1;
    }

    public int leaf() {
        return leaf.value();
    }
}
