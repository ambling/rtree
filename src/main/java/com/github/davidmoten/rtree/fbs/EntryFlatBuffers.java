package com.github.davidmoten.rtree.fbs;

import com.github.davidmoten.guavamini.Objects;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.fbs.generated.Entry_;
import com.github.davidmoten.rtree.geometry.Geometry;

/**
 * Index Entry backed by FlatBuffers.
 */
public class EntryFlatBuffers<T, S extends Geometry>
        implements Entry<T, S>, ObjFlatBuffers {

    private final int offset;
    private final ContextFlatBuffers<T, S> context;

    EntryFlatBuffers(Entry_ entry, ContextFlatBuffers<T, S> context) {
        this.context = context;
        this.offset = entry.__getOffset();
    }

    @Override
    public int offest() {
        return offset;
    }

    @Override
    public void set() {
        context.entry.__setOffset(offset);
    }

    @Override
    public T value() {
        set();
        return FlatBuffersHelper.parseObject(context.factory().deserializer(),
                context.entry);
    }

    @Override
    @SuppressWarnings("unchecked")
    public S geometry() {
        set();
        context.entry.geometry(context.geometry);
        return FlatBuffersHelper.toGeometry(context.geometry);
    }

    @Override
    public String toString() {
        String builder = "Entry [value=" +
                value() +
                ", geometry=" +
                geometry() +
                "]";
        return builder;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value(), geometry());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Entry) {
            Entry other = (Entry) obj;
            return Objects.equal(value(), other.value())
                    && Objects.equal(geometry(), other.geometry());
        } else
            return false;
    }
}
