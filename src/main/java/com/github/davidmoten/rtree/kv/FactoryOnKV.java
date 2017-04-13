package com.github.davidmoten.rtree.kv;

import com.github.davidmoten.rtree.*;
import com.github.davidmoten.rtree.geometry.Geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * The factory to create node on KV store.
 */
public final class FactoryOnKV<T, S extends Geometry> implements Factory<T, S> {
    private KVStore<T, S> kvStore;

    FactoryOnKV(KVStore<T, S> kvStore) {
        this.kvStore = kvStore;
    }

    @Override
    public Entry<T, S> createEntry(T value, S geometry) {
        String key = KVRTreeFactory.nextDataKey(kvStore);
        kvStore.putData(key, value);
        return new EntryOnKV<T, S>(geometry, key, kvStore);
    }

    @Override
    public Leaf<T, S> createLeaf(List<Entry<T, S>> entries, Context<T, S> context) {
        ArrayList<EntryOnKV<T, S>> entryOnKV = new ArrayList<EntryOnKV<T, S>>(entries.size());
        for (Entry<T, S> entry: entries) {
            if (entry instanceof EntryOnKV && kvStore == ((EntryOnKV) entry).kvStore) {
                entryOnKV.add((EntryOnKV<T, S>) entry);
            } else {
                entryOnKV.add((EntryOnKV<T, S>) createEntry(entry.value(), entry.geometry()));
            }
        }
        String key = KVRTreeFactory.nextNodeKey(kvStore);
        LeafOnKV<T, S> leaf = new LeafOnKV<T, S>(key, kvStore, entryOnKV, context);
        kvStore.putNode(key, leaf);
        return leaf;
    }

    @Override
    public NonLeaf<T, S> createNonLeaf(List<? extends Node<T, S>> children, Context<T, S> context) {
        ArrayList<NonLeafOnKV.Child> nodeOnKV = new ArrayList<NonLeafOnKV.Child>(children.size());
        for (Node<T, S> node: children) {
            NodeOnKV<T, S> onKV;
            if (node instanceof NodeOnKV && kvStore == ((NodeOnKV<T, S>) node).kvStore()) {
                onKV = (NodeOnKV<T, S>) node;
            } else {
                if (node instanceof Leaf) {
                    Leaf<T, S> leaf = (Leaf<T, S>) node;
                    onKV = (LeafOnKV<T, S>) createLeaf(leaf.entries(), leaf.context());
                } else {
                    NonLeaf<T, S> nonLeaf = (NonLeaf<T, S>) node;
                    onKV = (NonLeafOnKV<T, S>) createNonLeaf(nonLeaf.children(), nonLeaf.context());
                }
            }
            nodeOnKV.add(new NonLeafOnKV.Child(onKV.geometry().mbr(), onKV.key()));
        }
        String key = KVRTreeFactory.nextNodeKey(kvStore);
        NonLeafOnKV<T, S> nonLeaf = new NonLeafOnKV<T, S>(key, kvStore, nodeOnKV, context);
        kvStore.putNode(key, nonLeaf);
        return nonLeaf;
    }
}
