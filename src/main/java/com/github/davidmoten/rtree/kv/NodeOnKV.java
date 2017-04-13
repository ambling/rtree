package com.github.davidmoten.rtree.kv;

import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.geometry.Geometry;

public interface NodeOnKV<T, S extends Geometry> extends Node<T, S> {
    KVStore<T, S> kvStore();
    String key();
}
