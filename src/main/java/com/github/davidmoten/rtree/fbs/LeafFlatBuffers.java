package com.github.davidmoten.rtree.fbs;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree.Entries;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Leaf;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.fbs.generated.Box_;
import com.github.davidmoten.rtree.fbs.generated.Node_;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.internal.LeafHelper;
import com.github.davidmoten.rtree.internal.NodeAndEntries;
import rx.Subscriber;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;

import static com.github.davidmoten.rtree.fbs.FlatBuffersHelper.parseObject;
import static com.github.davidmoten.rtree.fbs.FlatBuffersHelper.toGeometry;

final class LeafFlatBuffers<T, S extends Geometry> implements Leaf<T, S>, ObjFlatBuffers{

    private final int offset;
    private final ContextFlatBuffers<T, S> context;

    LeafFlatBuffers(Node_ node, ContextFlatBuffers<T, S> context) {
        this.context = context;
        this.offset = node.__getOffset();
    }

    @Override
    public int offest() {
        return offset;
    }

    @Override
    public void set() {
        context.node.__setOffset(offset);
    }

    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry) {
        return LeafHelper.add(entry, this);
    }

    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        return LeafHelper.delete(entry, all, this);
    }

    @Override
    public void searchWithoutBackpressure(Func1<? super Geometry, Boolean> condition,
            Subscriber<? super Entry<T, S>> subscriber) {
        //only called when the root of the tree is a Leaf
        //normally the searchWithoutBackpressure is executed completely within the 
        //NonLeafFlatBuffers class to reduce object creation
        set();
        int numEntries = context.node.entriesLength();
        for (int i = 0; i < numEntries; i++) {
            if (subscriber.isUnsubscribed())
                return;
            // set entry
            context.node.entries(context.entry, i);
            // set geometry
            context.entry.geometry(context.geometry);
            final Geometry g = toGeometry(context.geometry);
            if (condition.call(g)) {
                T t = parseObject(context.factory().deserializer(), context.entry);
                Entry<T, S> ent = Entries.entry(t, (S) g);
                subscriber.onNext(ent);
            }
        }
    }

    @Override
    public int count() {
        set();
        return context.node.entriesLength();
    }

    @Override
    public ContextFlatBuffers<T, S> context() {
        return context;
    }

    @Override
    public Geometry geometry() {
        set();
        return context.node.mbb();
    }

    @Override
    public List<Entry<T, S>> entries() {
        set();
        int numEntries = context.node.entriesLength();
        List<Entry<T, S>> entries = new ArrayList<Entry<T, S>>(numEntries);
        Preconditions.checkArgument(numEntries > 0);
        for (int i = 0; i < numEntries; i++) {
            context.node.entries(context.entry, i);
            Entry<T, S> ent = new EntryFlatBuffers<T, S>(context.entry, context);
            entries.add(ent);
        }
        return entries;
    }

    @Override
    public Entry<T, S> entry(int i) {
        set();
        context.node.entries(context.entry, i);
        return new EntryFlatBuffers<T, S>(context.entry, context);
    }

}
