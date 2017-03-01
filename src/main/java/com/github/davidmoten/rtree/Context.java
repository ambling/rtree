package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;

/**
 * Configures an RTree prior to instantiation of an {@link RTree}.
 */
public interface Context<T, S extends Geometry> {

    public int maxChildren();

    public int minChildren();

    public Splitter splitter();

    public Selector selector();

    public Factory<T, S> factory();

}
