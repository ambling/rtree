package com.github.davidmoten.rtree.kv.store;

import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.kv.KVStore;
import com.github.davidmoten.rtree.kv.NodeOnKV;

import java.util.HashMap;

/**
 * A simple KVStore that backed by a hashmap.
 */
public class MapStore<T, S extends Geometry> implements KVStore<T, S> {

    private int size;
    private Context<T, S> context;
    private String rootKey;
    private final HashMap<String, NodeOnKV<T, S>> nodeMap;
    private final HashMap<String, T> dataMap;


    public MapStore() {
        size = 0;
        context = null;
        rootKey = null;
        nodeMap = new HashMap<String, NodeOnKV<T, S>>();
        dataMap = new HashMap<String, T>();
    }

    @Override
    public String rootKey() {
        return rootKey;
    }

    @Override
    public void setRootKey(String key) {
        rootKey = key;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void setContext(Context<T, S> context) {
        this.context = context;
    }

    @Override
    public Context<T, S> getContext() {
        return context;
    }

    @Override
    public int getNodeCnt() {
        return nodeMap.size();
    }

    @Override
    public void putNode(String key, NodeOnKV<T, S> node) {
        nodeMap.put(key, node);
    }

    @Override
    public NodeOnKV<T, S> getNode(String key) {
        return nodeMap.get(key);
    }

    @Override
    public int getDataCnt() {
        return dataMap.size();
    }

    @Override
    public void putData(String key, T value) {
        dataMap.put(key, value);
    }

    @Override
    public T getData(String key) {
        return dataMap.get(key);
    }
}
