package com.github.davidmoten.rtree.kv;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NonLeaf;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rtree.internal.NodeAndEntries;
import com.github.davidmoten.rtree.kryo.SerializerKryo;
import rx.Subscriber;
import rx.functions.Func1;

import java.util.List;

/**
 *
 * The non-leaf RTree node that is managed in a KVStore.
 */
public final class NonLeafOnKV<T, S extends Geometry> implements NonLeaf<T, S> {

    private final String key;
    private final KVStore kvStore;
    private final List<Child> children;
    private final Rectangle mbr;
    private final Context<T, S> context;
    private final SerializerKryo<T, S> serializer;

    public NonLeafOnKV(String key, KVStore kvStore, Context<T, S> context, SerializerKryo<T, S> serializer) {
        this.key = key;
        this.kvStore = kvStore;
        this.context = context;
        this.serializer = serializer;
        String nodeData = kvStore.get(key);
        nodeData.getBytes()
    }

    @Override
    public Geometry geometry() {
        return mbr;
    }

    @Override
    public Node<T, S> child(int i) {
        return null;
    }

    @Override
    public List<Node<T, S>> children() {
        return null;
    }

    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry) {
        return null;
    }

    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        return null;
    }

    @Override
    public void searchWithoutBackpressure(Func1<? super Geometry, Boolean> criterion, Subscriber<? super Entry<T, S>> subscriber) {

    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public Context<T, S> context() {
        return null;
    }

    public final class Child implements KryoSerializable {

        private Rectangle mbr;
        private String key;

        @Override
        public void write(Kryo kryo, Output output) {
            output.writeFloat(mbr.x1());
            output.writeFloat(mbr.y1());
            output.writeFloat(mbr.y1());
            output.writeFloat(mbr.y2());
            output.writeAscii(key);
        }

        @Override
        public void read(Kryo kryo, Input input) {
            key = input.readString();
            mbr = Geometries.rectangle(input.readFloat(), input.readFloat(), input.readFloat(), input.readFloat());
        }
    }
}
