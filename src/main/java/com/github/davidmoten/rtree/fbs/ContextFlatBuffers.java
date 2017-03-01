package com.github.davidmoten.rtree.fbs;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Selector;
import com.github.davidmoten.rtree.Splitter;
import com.github.davidmoten.rtree.fbs.generated.*;
import com.github.davidmoten.rtree.geometry.Geometry;

/**
 * The shared context with singleton generated class objects
 */
public class ContextFlatBuffers<T, S extends Geometry> implements Context<T, S> {

    private final int maxChildren;
    private final int minChildren;
    private final Splitter splitter;
    private final Selector selector;
    private final FactoryFlatBuffers<T, S> factory;

    Node_ node;
    Node_ child;
    Entry_ entry;
    Geometry_ geometry;
    Box_ box;
    Point_ point;
    Line_ line;
    Circle_ circle;

    public ContextFlatBuffers(int minChildren, int maxChildren, Selector selector,
                              Splitter splitter, FactoryFlatBuffers<T, S> factory) {
        Preconditions.checkNotNull(splitter);
        Preconditions.checkNotNull(selector);
        Preconditions.checkArgument(maxChildren > 2);
        Preconditions.checkArgument(minChildren >= 1);
        Preconditions.checkArgument(minChildren < maxChildren);
        Preconditions.checkNotNull(factory);
        this.selector = selector;
        this.maxChildren = maxChildren;
        this.minChildren = minChildren;
        this.splitter = splitter;
        this.factory = factory;

        node = new Node_();
        child = new Node_();
        entry = new Entry_();
        geometry = new Geometry_();
        box = new Box_();
        point = new Point_();
        line = new Line_();
        circle = new Circle_();
    }

    @Override
    public int maxChildren() {
        return maxChildren;
    }

    @Override
    public int minChildren() {
        return minChildren;
    }

    @Override
    public Splitter splitter() {
        return splitter;
    }

    @Override
    public Selector selector() {
        return selector;
    }

    @Override
    public FactoryFlatBuffers<T, S> factory() {
        return factory;
    }

}
