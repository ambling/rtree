package com.github.davidmoten.rtree.kv.store.redis;

import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.kv.KVStore;
import com.github.davidmoten.rtree.kv.NodeOnKV;
import redis.clients.jedis.Jedis;

import java.io.Serializable;

/**
 *
 * Created by mgh on 4/14/17.
 */
public class RedisStore<T, S extends Geometry> implements KVStore<T, S> {

    private int size;
    private int nodeCount;
    private int dataCount
    private String dataName;
    private Context<T, S> context;
    private String rootKey;
    private final Jedis jedis;

    public RedisStore(String host, String dataName) {
        this.dataName = dataName;
        jedis = new Jedis(host);
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
    public void setSize(int size) { // tree size
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
        return nodeCount;
    }

    @Override
    public void putNode(String key, NodeOnKV<T , S> node) {
        String serializedNode = RedisUtil.toString(node); // serializable string
        jedis.set(RedisUtil.redisKey(dataName, key), serializedNode);
        nodeCount++;
    }

    @Override
    public NodeOnKV<T, S> getNode(String key) {
        return (NodeOnKV<T, S>) RedisUtil.fromString(jedis.get(RedisUtil.redisKey(dataName, key)));
    }

    @Override
    public int getDataCnt() {
        return dataCount;
    }

    @Override
    public void putData(String key, T value) {
        jedis.set(RedisUtil.redisKey(dataName, key), RedisUtil.toString(value));
        dataCount++;
    }

    @Override
    public T getData(String key) {
        return (T) RedisUtil.fromString(jedis.get(RedisUtil.redisKey(dataName, key)));
    }
}
