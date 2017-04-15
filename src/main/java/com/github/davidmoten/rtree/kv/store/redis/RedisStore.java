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

    private String dataName;
    private final Jedis jedis;

    public RedisStore(String host, String dataName) {
        this.dataName = dataName;
        jedis = new Jedis(host);
    }
    @Override
    public String rootKey() {
        return jedis.get(RedisUtil.redisKey(dataName, RedisUtil.ROOT_NAME));
    }

    @Override
    public void setRootKey(String key) {
        jedis.set(RedisUtil.redisKey(dataName, RedisUtil.ROOT_NAME), key);
    }

    @Override
    public void setSize(int size) {
        jedis.set(RedisUtil.redisKey(dataName, RedisUtil.SIZE),
                  RedisUtil.toString(size));
    }

    @Override
    public int getSize() {
        return jedis.get(RedisUtil.redisKey(dataName, RedisUtil.SIZE));
    }

    @Override
    public void setContext(Context<T, S> context) {
        jedis.set(RedisUtil.redisKey(dataName, RedisUtil.CONTEXT),
                  RedisUtil.toString(context));
    }

    @Override
    public Context<T, S> getContext() {
        return (Context<T, S>) RedisUtil.fromString(jedis.get(RedisUtil.redisKey(dataName, RedisUtil.CONTEXT)));
    }

    @Override
    public int getNodeCnt() {
        return (int) RedisUtil.fromString(jedis.get(RedisUtil.redisKey(dataName, RedisUtil.NODE_COUNT)));
    }

    @Override
    public void putNode(String key, NodeOnKV<T , S> node) {
        String serializedNode = RedisUtil.toString(node);
        jedis.put(RedisUtil.redisKey(dataName, serializedNode));
    }

    @Override
    public NodeOnKV<T, S> getNode(String key) {
        return (NodeOnKV<T, S>) RedisUtil.fromString(jedis.get(RedisUtil.redisKey(dataName, key)));
    }

    @Override
    public int getDataCnt() {
        return (int) RedisUtil.fromString(jedis.get(RedisUtil.redisKey(dataName, RedisUtil.DATA_COUNT)));
    }

    @Override
    public void putData(String key, T value) {
        jedis.set(RedisUtil.redisKey(dataName, key), RedisUtil.toString(value));
    }

    @Override
    public T getData(String key) {
        return (T) RedisUtil.fromString(jedis.get(RedisUtil.redisKey(dataName, key)));
    }
}
