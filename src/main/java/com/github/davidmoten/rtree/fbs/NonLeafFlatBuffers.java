package com.github.davidmoten.rtree.fbs;

import com.github.davidmoten.rtree.*;
import com.github.davidmoten.rtree.fbs.generated.Box_;
import com.github.davidmoten.rtree.fbs.generated.Node_;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.internal.NodeAndEntries;
import com.github.davidmoten.rtree.internal.NonLeafHelper;
import rx.Subscriber;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;

import static com.github.davidmoten.rtree.fbs.FlatBuffersHelper.parseObject;
import static com.github.davidmoten.rtree.fbs.FlatBuffersHelper.toGeometry;

final class NonLeafFlatBuffers<T, S extends Geometry> implements NonLeaf<T, S>, ObjFlatBuffers {

    private final int offset;
    private final ContextFlatBuffers<T, S> context;


    NonLeafFlatBuffers(Node_ node, ContextFlatBuffers<T, S> context) {
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
        return NonLeafHelper.add(entry, this);
    }

    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        return NonLeafHelper.delete(entry, all, this);
    }

    @Override
    public void searchWithoutBackpressure(Func1<? super Geometry, Boolean> criterion,
            Subscriber<? super Entry<T, S>> subscriber) {
        // pass through entry and geometry and box instances to be reused for
        // flatbuffers extraction this reduces allocation/gc costs (but of
        // course introduces some mutable ugliness into the codebase)
        set();
        searchWithoutBackpressure(context.node, criterion, subscriber, context);
    }

    @SuppressWarnings("unchecked")
    private static <T, S extends Geometry> void searchWithoutBackpressure(Node_ node,
            Func1<? super Geometry, Boolean> criterion, Subscriber<? super Entry<T, S>> subscriber,
            ContextFlatBuffers<T, S> context) {
        Box_ mbb = context.node.mbb(context.box);
        if (!criterion.call(mbb)) return;

        int numChildren = node.childrenLength();
        // reduce allocations by reusing objects
        Node_ child = new Node_();
        if (numChildren > 0) {
            for (int i = 0; i < numChildren; i++) {
                if (subscriber.isUnsubscribed())
                    return;
                node.children(child, i);
                searchWithoutBackpressure(child, criterion, subscriber, context);
            }
        } else {
            int numEntries = node.entriesLength();
            // reduce allocations by reusing objects
            // check all entries
            for (int i = 0; i < numEntries; i++) {
                if (subscriber.isUnsubscribed())
                    return;
                // set entry
                node.entries(context.entry, i);
                // set geometry
                context.entry.geometry(context.geometry);
                final Geometry g = toGeometry(context.geometry);
                if (criterion.call(g)) {
                    T t = parseObject(context.factory().deserializer(), context.entry);
                    Entry<T, S> ent = Entries.entry(t, (S) g);
                    subscriber.onNext(ent);
                }
            }
        }

    }

    @Override
    public int count() {
        set();
        return context.node.childrenLength();
    }

    @Override
    public Context<T, S> context() {
        return context;
    }

    @Override
    public Geometry geometry() {
        set();
        return context.node.mbb();
    }

    @Override
    public Node<T, S> child(int i) {
        set();
        context.node.children(context.child, i);
        if (context.child.childrenLength() > 0)
            return new NonLeafFlatBuffers<T, S>(context.child, context);
        else
            return new LeafFlatBuffers<T, S>(context.child, context);
    }

    @Override
    public List<Node<T, S>> children() {
        set();
        int numChildren = context.node.childrenLength();
        List<Node<T, S>> children = new ArrayList<Node<T, S>>(numChildren);
        for (int i = 0; i < numChildren; i++) {
            children.add(child(i));
        }
        return children;
    }

    @Override
    public String toString() {
        set();
        return "Node [" + (context.node.childrenLength() > 0 ? "NonLeaf" : "Leaf") + ","
                + context.node.mbb(context.box).toString() + "]";
    }

}
