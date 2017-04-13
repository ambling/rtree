package com.github.davidmoten.rtree.kv;

import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.geometry.Geometry;

/**
 * The interface of a key-value store for RTree.
 *
 * An RTree backed by a KV store (which may be off-heap or out of JVM) can save
 * pressure on GC (in case the dataset is in large volumn).
 *
 * To support the RTree storage, the KV store should have meta data (e.g. context and size),
 * node store and data store.
 *
 * Serialization may be implemented under these interfaces.
 */
public interface KVStore<T, S extends Geometry> {

    String rootKey();

    void setRootKey(String key);

    void setSize(int size);

    int getSize();

    void setContext(Context<T, S> context);

    Context<T, S> getContext();

    int getNodeCnt();

    void putNode(String key, NodeOnKV<T, S> node);

    NodeOnKV<T, S> getNode(String key);

    int getDataCnt();

    void putData(String key, T value);

    T getData(String key);

}
