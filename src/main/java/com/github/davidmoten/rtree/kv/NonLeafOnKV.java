package com.github.davidmoten.rtree.kv;

import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NonLeaf;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rtree.internal.NodeAndEntries;
import com.github.davidmoten.rtree.internal.NonLeafHelper;
import com.github.davidmoten.rtree.internal.Util;
import rx.Subscriber;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * The non-leaf RTree node that is managed in a KVStore.
 */
public final class NonLeafOnKV<T, S extends Geometry> implements NodeOnKV<T, S>, NonLeaf<T, S> {

    private final String key;
    private final KVStore<T, S> kvStore;
    private final List<Child> children;
    private final Rectangle mbr;
    private final Context<T, S> context;

    NonLeafOnKV(String key, KVStore<T, S> kvStore, List<Child> children, Context<T, S> context) {
        this.key = key;
        this.kvStore = kvStore;
        this.children = children;
        this.context = context;
        this.mbr = Util.mbr(children);
    }

    @Override
    public Geometry geometry() {
        return mbr;
    }

    @Override
    public Node<T, S> child(int i) {
        return kvStore.getNode(children.get(i).key);
    }

    @Override
    public List<Node<T, S>> children() {
        List<Node<T, S>> nodes = new ArrayList<Node<T, S>>(count());
        for (Child child: children) {
            nodes.add(kvStore.getNode(child.key));
        }
        return nodes;
    }

    /**
     * Note: modification of RTree may lead to redundant data in underlying KV store due to the immutability.
     */
    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry) {

        if (!(entry instanceof EntryOnKV && kvStore == ((EntryOnKV) entry).kvStore))
            entry = context.factory().createEntry(entry.value(), entry.geometry());
        return NonLeafHelper.add(entry, this);
    }

    /**
     * Note: modification of RTree may lead to redundant data in underlying KV store due to the immutability.
     */
    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        return NonLeafHelper.delete(entry, all, this);
    }

    /**
     * Save some creation of children nodes.
     */
    @Override
    public void searchWithoutBackpressure(Func1<? super Geometry, Boolean> criterion,
                                          Subscriber<? super Entry<T, S>> subscriber) {
        if (!criterion.call(mbr)) return;

        for (int i = 0; i < count(); i++) {
            if (subscriber.isUnsubscribed()) {
                return;
            } else {
                Child child = children.get(i);
                if (criterion.call(child.mbr))
                    kvStore.getNode(child.key).searchWithoutBackpressure(criterion, subscriber);
            }
        }
    }

    @Override
    public int count() {
        return children == null ? 0 : children.size();
    }

    @Override
    public Context<T, S> context() {
        return context;
    }

    @Override
    public KVStore<T, S> kvStore() {
        return kvStore;
    }

    @Override
    public String key() {
        return key;
    }

    final static class Child implements HasGeometry {

        Rectangle mbr;
        String key;

        Child(Rectangle mbr, String key) {
            this.mbr = mbr;
            this.key = key;
        }

        @Override
        public Geometry geometry() {
            return mbr;
        }
    }
}
