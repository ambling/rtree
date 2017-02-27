package com.github.davidmoten.rtree.internal;

import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometry;

import static com.github.davidmoten.guavamini.Optional.of;

/**
 * Immutable in-memory 2D R-Tree with configurable splitter heuristic.
 *
 * @param <T>
 *            the entry value type
 * @param <S>
 *            the entry geometry type
 */
public final class RTreeDefault<T, S extends Geometry> extends RTree<T, S> {

    private final Optional<? extends Node<T, S>> root;
    private final Context<T, S> context;

    /**
     * Current size in Entries of the RTree.
     */
    private final int size;

    @Override
    public Optional<? extends Node<T, S>> root() {
        return root;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Context<T, S> context() {
        return context;
    }

    /**
     * Constructor.
     *
     * @param root
     *            the root node of the tree if present
     * @param context
     *            options for the R-tree
     */
    public RTreeDefault(Optional<? extends Node<T, S>> root, int size, Context<T, S> context) {
        this.root = root;
        this.size = size;
        this.context = context;
    }

    public RTreeDefault() {
        this(Optional.<Node<T, S>> absent(), 0, null);
    }

    /**
     * Constructor.
     *
     * @param root
     *            the root node of the R-tree
     * @param context
     *            options for the R-tree
     */
    public RTreeDefault(Node<T, S> root, int size, Context<T, S> context) {
        this(of(root), size, context);
    }


    @Override
    protected RTree<T, S> create() {
        return new RTreeDefault<T, S>();
    }

    @Override
    protected RTree<T, S> create(Node<T, S> root, int size, Context<T, S> context) {
        return new RTreeDefault<T, S>(root, size, context);
    }

    @Override
    protected RTree<T, S> create(Optional<? extends Node<T, S>> root, int size, Context<T, S> context) {
        return new RTreeDefault<T, S>(root, size, context);
    }

}
