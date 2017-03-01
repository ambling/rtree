package com.github.davidmoten.rtree.fbs;

import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometry;

/**
 * Immutable in-memory 2D RTree on FlatBuffers.
 *
 * To avoid unnecessary allocation, the data read from FlatBuffers is used
 * directly through the generated classes, and algorithms are overrided to
 * support maximum reuse of objects.
 *
 * In a modified RTree, newly added objects (including nodes, entries and
 * geometries) are created in their default implementations.
 *
 * @param <T>
 *            the entry value type
 * @param <S>
 *            the entry geometry type
 */
public class RTreeFlatBuffers<T, S extends Geometry> extends RTree<T, S> {
    @Override
    public Optional<? extends Node<T, S>> root() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Context<T, S> context() {
        return null;
    }

    @Override
    protected RTree<T, S> create() {
        return null;
    }

    @Override
    protected RTree<T, S> create(Node<T, S> root, int size, Context<T, S> context) {
        return null;
    }

    @Override
    protected RTree<T, S> create(Optional<? extends Node<T, S>> root, int size, Context<T, S> context) {
        return null;
    }
}
