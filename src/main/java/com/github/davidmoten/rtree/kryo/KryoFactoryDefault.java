package com.github.davidmoten.rtree.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.rtree.*;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rtree.internal.LeafDefault;
import com.github.davidmoten.rtree.internal.NonLeafDefault;
import rx.functions.Func0;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;

/**
 * A default factory of kryo for RTree with necessary registration.
 */
public class KryoFactoryDefault<T, S extends Geometry> implements Func0<Kryo> {

    private final Func1<? super T, byte[]> serializer;
    private final Func1<byte[], ? extends T> deserializer;

    /**
     * Use the default kryo serializer and deserializer for objects.
     */
    public KryoFactoryDefault() {
        serializer = null;
        deserializer = null;
    }

    public KryoFactoryDefault(Func1<? super T, byte[]> serializer, Func1<byte[], ? extends T> deserializer) {
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public Kryo call() {
        Kryo kryo = new Kryo();
        //TODO need to handle more types of geometry.
        kryo.register(Point.class, new PointSerializer());
        kryo.register(RTree.class, new RTreeSerializer<T, S>(serializer, deserializer));

        return kryo;
    }

    public static class PointSerializer extends Serializer<Point> {

        @Override
        public void write(Kryo kryo, Output output, Point object) {
            output.writeFloat(object.x());
            output.writeFloat(object.y());
        }

        @Override
        public Point read(Kryo kryo, Input input, Class<Point> type) {
            float x = input.readFloat();
            float y = input.readFloat();
            return Geometries.point(x, y);
        }
    }

    /**
     * A kryo serializer for RTree.
     */
    public static class RTreeSerializer<T, S extends Geometry> extends Serializer<RTree<T, S>> {

        private final Func1<? super T, byte[]> serializer;
        private final Func1<byte[], ? extends T> deserializer;

        /**
         * Use the default kryo serializer and deserializer for objects.
         */
        public RTreeSerializer() {
            serializer = null;
            deserializer = null;
        }

        public RTreeSerializer(Func1<? super T, byte[]> serializer, Func1<byte[], ? extends T> deserializer) {
            this.serializer = serializer;
            this.deserializer = deserializer;
        }

        @Override
        public void write(Kryo kryo, Output output, RTree<T, S> rtree) {
            output.writeInt(rtree.size());
            kryo.writeClassAndObject(output, rtree.context());
            if (rtree.size() > 0) {
                NodeSerializer<T, S> nodeSerializer =
                        new NodeSerializer<T, S>(serializer, deserializer, rtree.context());
                kryo.register(LeafDefault.class, nodeSerializer);
                kryo.register(NonLeafDefault.class, nodeSerializer);
                kryo.writeClassAndObject(output, rtree.root().get());
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public RTree<T, S> read(Kryo kryo, Input input, Class<RTree<T, S>> type) {
            int size = input.readInt();
            Context<T, S> context = (Context<T, S>) kryo.readClassAndObject(input);
            Optional<? extends Node<T, S>> root = Optional.absent();
            if (size > 0) {
                NodeSerializer<T, S> nodeSerializer = new NodeSerializer<T, S>(serializer, deserializer, context);
                kryo.register(LeafDefault.class, nodeSerializer);
                kryo.register(NonLeafDefault.class, nodeSerializer);
                root = Optional.of((Node<T, S>) kryo.readClassAndObject(input));
            }

            return RTree.create(root, size, context);
        }
    }

    public static class NodeSerializer<T, S extends Geometry> extends Serializer<Node<T, S>> {

        private final Func1<? super T, byte[]> serializer;
        private final Func1<byte[], ? extends T> deserializer;
        private final Context<T, S> context;

        /**
         * Use the default kryo serializer and deserializer for objects.
         */
        public NodeSerializer(Context<T, S> context) {
            serializer = null;
            deserializer = null;
            this.context = context;
        }

        public NodeSerializer(Func1<? super T, byte[]> serializer, Func1<byte[], ? extends T> deserializer,
                              Context<T, S> context) {
            this.serializer = serializer;
            this.deserializer = deserializer;
            this.context = context;
        }

        @Override
        public void write(Kryo kryo, Output output, Node<T, S> node) {
            boolean isLeaf = node instanceof Leaf;
            output.writeBoolean(isLeaf);
            if (isLeaf) {
                Leaf<T, S> leaf = (Leaf<T, S>) node;
//                writeBounds(output, leaf.geometry().mbr());
                output.writeInt(leaf.count());
                for (Entry<T, S> entry : leaf.entries()) {
                    S g = entry.geometry();
                    kryo.writeClassAndObject(output, g);
                    writeValue(kryo, output, entry.value());
                }
            } else {
                NonLeaf<T, S> nonLeaf = (NonLeaf<T, S>) node;
//                writeBounds(output, nonLeaf.geometry().mbr());
                output.writeInt(nonLeaf.count());
                for (Node<T, S> nd : nonLeaf.children()) {
                    kryo.writeClassAndObject(output, nd);
                }
            }
        }

        @Override
        public Node<T, S> read(Kryo kryo, Input input, Class<Node<T, S>> type) {
            boolean isLeaf = input.readBoolean();
            int count = input.readInt();
            Factory<T, S> factory = Factories.defaultFactory();
            if (isLeaf) {
                List<Entry<T, S>> entries = new ArrayList<Entry<T, S>>(count);
                for (int i = 0; i < count; ++i) {
                    S g = (S) kryo.readClassAndObject(input);
                    T value = readValue(kryo, input);
                    entries.add(Entries.entry(value, g));
                }
                return factory.createLeaf(entries, context);
            } else {
                List<Node<T, S>> children = new ArrayList<Node<T, S>>(count);
                for (int i = 0; i < count; ++i) {
                    Node<T, S> node = (Node<T, S>) kryo.readClassAndObject(input);
                    children.add(node);
                }
                return factory.createNonLeaf(children, context);
            }
        }

        private void writeBounds(Output output, Rectangle mbr) {
            output.writeFloat(mbr.x1());
            output.writeFloat(mbr.y1());
            output.writeFloat(mbr.y1());
            output.writeFloat(mbr.y2());
        }

        private void writeValue(Kryo kryo, Output output, T t) {
            if (serializer == null) {
                kryo.writeClassAndObject(output, t);
            } else {
                byte[] bytes = serializer.call(t);
                output.writeInt(bytes.length);
                output.write(bytes);
            }
        }

        @SuppressWarnings("unchecked")
        private T readValue(Kryo kryo, Input input) {
            if (deserializer == null) {
                return (T) kryo.readClassAndObject(input);
            } else {
                int length = input.readInt();
                byte[] data = input.readBytes(length);
                return deserializer.call(data);
            }
        }
    }
}
