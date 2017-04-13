package com.github.davidmoten.rtree.kv;

import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Leaf;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rtree.internal.LeafHelper;
import com.github.davidmoten.rtree.internal.NodeAndEntries;
import com.github.davidmoten.rtree.internal.Util;
import rx.Subscriber;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;

public final class LeafOnKV<T, S extends Geometry> implements NodeOnKV<T, S>, Leaf<T, S> {

    private final String key;
    private final KVStore<T, S> kvStore;
    private final List<EntryOnKV<T, S>> entries;
    private final Rectangle mbr;
    private final Context<T, S> context;

    LeafOnKV(String key, KVStore<T, S> kvStore, List<EntryOnKV<T, S>> entries, Context<T, S> context) {
        this.key = key;
        this.kvStore = kvStore;
        this.entries = entries;
        this.context = context;
        this.mbr = Util.mbr(entries);
    }

    @Override
    public Geometry geometry() {
        return mbr;
    }

    @Override
    public List<Entry<T, S>> entries() {
        ArrayList<Entry<T, S>> re = new ArrayList<Entry<T, S>>(entries.size());
        re.addAll(entries);
        return re;
    }

    @Override
    public Entry<T, S> entry(int i) {
        return entries.get(i);
    }

    @Override
    public KVStore<T, S> kvStore() {
        return kvStore;
    }

    @Override
    public String key() {
        return key;
    }

    /**
     * Note: modification of RTree may lead to redundant data in underlying KV store due to the immutability.
     */
    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry) {
        if (!(entry instanceof EntryOnKV && kvStore == ((EntryOnKV) entry).kvStore))
            entry = context.factory().createEntry(entry.value(), entry.geometry());
        return LeafHelper.add(entry, this);
    }

    /**
     * Note: modification of RTree may lead to redundant data in underlying KV store due to the immutability.
     */
    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        return LeafHelper.delete(entry, all, this);
    }

    @Override
    public void searchWithoutBackpressure(Func1<? super Geometry, Boolean> criterion,
                                          Subscriber<? super Entry<T, S>> subscriber) {
        LeafHelper.search(criterion, subscriber, this);
    }

    @Override
    public int count() {
        return entries == null ? 0 : entries.size();
    }

    @Override
    public Context<T, S> context() {
        return context;
    }
}
