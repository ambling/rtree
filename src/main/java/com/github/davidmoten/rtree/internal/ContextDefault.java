package com.github.davidmoten.rtree.internal;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree.*;
import com.github.davidmoten.rtree.geometry.Geometry;

/**
 * @author ambling
 */
public class ContextDefault<T, S extends Geometry> implements Context<T, S> {

    private final int maxChildren;
    private final int minChildren;
    private final Splitter splitter;
    private final Selector selector;
    private final Factory<T, S> factory;

    /**
     * Constructor.
     *
     * @param minChildren
     *            minimum number of children per node (at least 1)
     * @param maxChildren
     *            max number of children per node (minimum is 3)
     * @param selector
     *            algorithm to select search path
     * @param splitter
     *            algorithm to split the children across two new nodes
     * @param factory
     *            node creation factory
     */
    public ContextDefault(int minChildren, int maxChildren, Selector selector,
                          Splitter splitter, Factory<T, S> factory) {
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
    }

    private ContextDefault() {
        this(2, 4, new SelectorMinimalAreaIncrease(),
                new SplitterQuadratic(), Factories.<T, S> defaultFactory());
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
    public Factory<T, S> factory() {
        return factory;
    }
}
