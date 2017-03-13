package com.github.davidmoten.rtree;

import static com.github.davidmoten.rtree.Utilities.entries1000;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.github.davidmoten.rtree.fbs.SerializerFlatBuffers;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;

import rx.Subscriber;
import rx.functions.Func1;

@State(Scope.Benchmark)
public class BenchmarksRTree {

    // load the datasets, with 70% of the data to construct rtree, and 30% left for batch insertion

    private final List<Entry<Object, Point>> entriesGreek = GreekEarthquakes.entriesList();

    private final List<Entry<Object, Point>> entriesGreek07 =
            entriesGreek.subList(0, (int) (0.7 * entriesGreek.size()));

    private final List<Entry<Object, Point>> entriesGreek03 =
            entriesGreek.subList((int) (0.7 * entriesGreek.size()), entriesGreek.size());

    private final List<Entry<Object, Rectangle>> entries1k = entries1000();

    private final List<Entry<Object, Rectangle>> entries1k07 =
            entries1k.subList(0, (int) (0.7 * entries1k.size()));

    private final List<Entry<Object, Rectangle>> entries1k03 =
            entries1k.subList((int) (0.7 * entries1k.size()), entries1k.size());

    // construct rtrees, at now there are [defaultTree, starTree, strTree, fbsTree] with max children [4, 10, 32, 128]

    private final RTree<Object, Point> defaultTreeGreekM4 = RTree.maxChildren(4)
            .<Object, Point> create().add(entriesGreek07);

    private final RTree<Object, Point> defaultTreeGreekM10 = RTree.maxChildren(10)
            .<Object, Point> create().add(entriesGreek07);

    private final RTree<Object, Point> defaultTreeGreekM32 = RTree.maxChildren(32)
            .<Object, Point> create().add(entriesGreek07);

    private final RTree<Object, Point> defaultTreeGreekM128 = RTree.maxChildren(128)
            .<Object, Point> create().add(entriesGreek07);

    private final RTree<Object, Point> starTreeGreekM4 = RTree.maxChildren(4).star()
            .<Object, Point> create().add(entriesGreek07);

    private final RTree<Object, Point> starTreeGreekM10 = RTree.maxChildren(10).star()
            .<Object, Point> create().add(entriesGreek07);

    private final RTree<Object, Point> starTreeGreekM32 = RTree.maxChildren(32).star()
            .<Object, Point> create().add(entriesGreek07);

    private final RTree<Object, Point> starTreeGreekM128 = RTree.maxChildren(128).star()
            .<Object, Point> create().add(entriesGreek07);

    private final RTree<Object, Point> strTreeGreekM4 = RTree.maxChildren(4)
            .<Object, Point> create(entriesGreek07);

    private final RTree<Object, Point> strTreeGreekM10 = RTree.maxChildren(10)
            .<Object, Point> create(entriesGreek07);

    private final RTree<Object, Point> strTreeGreekM32 = RTree.maxChildren(32)
            .<Object, Point> create(entriesGreek07);

    private final RTree<Object, Point> strTreeGreekM128 = RTree.maxChildren(128)
            .<Object, Point> create(entriesGreek07);

    private final RTree<Object, Rectangle> defaultTree1kM4 = RTree.maxChildren(4)
            .<Object, Rectangle> create().add(entries1k07);

    private final RTree<Object, Rectangle> defaultTree1kM10 = RTree.maxChildren(10)
            .<Object, Rectangle> create().add(entries1k07);

    private final RTree<Object, Rectangle> defaultTree1kM32 = RTree.maxChildren(32)
            .<Object, Rectangle> create().add(entries1k07);

    private final RTree<Object, Rectangle> defaultTree1kM128 = RTree.maxChildren(128)
            .<Object, Rectangle> create().add(entries1k07);

    private final RTree<Object, Rectangle> starTree1kM4 = RTree.maxChildren(4).star()
            .<Object, Rectangle> create().add(entries1k07);

    private final RTree<Object, Rectangle> starTree1kM10 = RTree.maxChildren(10).star()
            .<Object, Rectangle> create().add(entries1k07);

    private final RTree<Object, Rectangle> starTree1kM32 = RTree.maxChildren(32).star()
            .<Object, Rectangle> create().add(entries1k07);

    private final RTree<Object, Rectangle> starTree1kM128 = RTree.maxChildren(128).star()
            .<Object, Rectangle> create().add(entries1k07);

    private final RTree<Object, Rectangle> strTree1kM4 = RTree.maxChildren(4)
            .<Object, Rectangle> create(entries1k07);

    private final RTree<Object, Rectangle> strTree1kM10 = RTree.maxChildren(10)
            .<Object, Rectangle> create(entries1k07);

    private final RTree<Object, Rectangle> strTree1kM32 = RTree.maxChildren(32)
            .<Object, Rectangle> create(entries1k07);

    private final RTree<Object, Rectangle> strTree1kM128 = RTree.maxChildren(128)
            .<Object, Rectangle> create(entries1k07);

    private final byte[] byteArrayGreekM4 = createFBSByteArray(starTreeGreekM4);

    private final byte[] byteArrayGreekM10 = createFBSByteArray(starTreeGreekM10);

    private final byte[] byteArrayGreekM32 = createFBSByteArray(starTreeGreekM32);

    private final byte[] byteArrayGreekM128 = createFBSByteArray(starTreeGreekM128);

    private final byte[] byteArray1kM4 = createFBSByteArray(starTree1kM4);

    private final byte[] byteArray1kM10 = createFBSByteArray(starTree1kM10);

    private final byte[] byteArray1kM32 = createFBSByteArray(starTree1kM32);

    private final byte[] byteArray1kM128 = createFBSByteArray(starTree1kM128);

    private final RTree<Object, Point> fbsTreeGreekM4 = createFBSTree(byteArrayGreekM4);

    private final RTree<Object, Point> fbsTreeGreekM10 = createFBSTree(byteArrayGreekM10);

    private final RTree<Object, Point> fbsTreeGreekM32 = createFBSTree(byteArrayGreekM32);

    private final RTree<Object, Point> fbsTreeGreekM128 = createFBSTree(byteArrayGreekM128);

    private final RTree<Object, Rectangle> fbsTree1kM4 = createFBSTree(byteArray1kM4);

    private final RTree<Object, Rectangle> fbsTree1kM10 = createFBSTree(byteArray1kM10);

    private final RTree<Object, Rectangle> fbsTree1kM32 = createFBSTree(byteArray1kM32);

    private final RTree<Object, Rectangle> fbsTree1kM128 = createFBSTree(byteArray1kM128);

    private <Object, S extends Geometry> byte[] createFBSByteArray(RTree<Object, S> tree) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        Func1<Object, byte[]> serializer = new Func1<Object, byte[]>() {
            @Override
            public byte[] call(Object o) {
                return new byte[0];
            }
        };
        Func1<byte[], Object> deserializer = new Func1<byte[], Object>() {
            @Override
            public Object call(byte[] bytes) {
                return null;
            }
        };
        Serializer<Object, S> fbSerializer = SerializerFlatBuffers.create(serializer,
                deserializer);
        try {
            fbSerializer.write(tree, os);
            os.close();
            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <Object, S extends Geometry> RTree<Object, S> createFBSTree(byte [] byteArray) {
        Func1<Object, byte[]> serializer = new Func1<Object, byte[]>() {
            @Override
            public byte[] call(Object o) {
                return new byte[0];
            }
        };
        Func1<byte[], Object> deserializer = new Func1<byte[], Object>() {
            @Override
            public Object call(byte[] bytes) {
                return null;
            }
        };
        Serializer<Object, S> fbSerializer = SerializerFlatBuffers.create(serializer,
                deserializer);
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(byteArray);
            return fbSerializer.read(is, byteArray.length, InternalStructure.SINGLE_ARRAY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // benchmark tree construction

    @Benchmark
    public void defaultTreeGreekM4Construction() {
        RTree.maxChildren(4).<Object, Point> create().add(entriesGreek07);
    }

    @Benchmark
    public void defaultTreeGreekM10Construction() {
        RTree.maxChildren(10).<Object, Point> create().add(entriesGreek07);
    }

    @Benchmark
    public void defaultTreeGreekM32Construction() {
        RTree.maxChildren(32).<Object, Point> create().add(entriesGreek07);
    }

    @Benchmark
    public void defaultTreeGreekM128Construction() {
        RTree.maxChildren(128).<Object, Point> create().add(entriesGreek07);
    }


    @Benchmark
    public void starTreeGreekM4Construction() {
        RTree.maxChildren(4).star().<Object, Point> create().add(entriesGreek07);
    }

    @Benchmark
    public void starTreeGreekM10Construction() {
        RTree.maxChildren(10).star().<Object, Point> create().add(entriesGreek07);
    }

    @Benchmark
    public void starTreeGreekM32Construction() {
        RTree.maxChildren(32).star().<Object, Point> create().add(entriesGreek07);
    }

    @Benchmark
    public void starTreeGreekM128Construction() {
        RTree.maxChildren(128).star().<Object, Point> create().add(entriesGreek07);
    }


    @Benchmark
    public void strTreeGreekM4Construction() {
        RTree.maxChildren(4).<Object, Point> create(entriesGreek07);
    }

    @Benchmark
    public void strTreeGreekM10Construction() {
        RTree.maxChildren(10).<Object, Point> create(entriesGreek07);
    }

    @Benchmark
    public void strTreeGreekM32Construction() {
        RTree.maxChildren(32).<Object, Point> create(entriesGreek07);
    }

    @Benchmark
    public void strTreeGreekM128Construction() {
        RTree.maxChildren(128).<Object, Point> create(entriesGreek07);
    }


    @Benchmark
    public void fbsTreeGreekM4Construction() {
        createFBSTree(byteArrayGreekM4);
    }

    @Benchmark
    public void fbsTreeGreekM10Construction() {
        createFBSTree(byteArrayGreekM10);
    }

    @Benchmark
    public void fbsTreeGreekM32Construction() {
        createFBSTree(byteArrayGreekM32);
    }

    @Benchmark
    public void fbsTreeGreekM128Construction() {
        createFBSTree(byteArrayGreekM128);
    }


    @Benchmark
    public void defaultTree1kM4Construction() {
        RTree.maxChildren(4).<Object, Rectangle> create().add(entries1k07);
    }

    @Benchmark
    public void defaultTree1kM10Construction() {
        RTree.maxChildren(10).<Object, Rectangle> create().add(entries1k07);
    }

    @Benchmark
    public void defaultTree1kM32Construction() {
        RTree.maxChildren(32).<Object, Rectangle> create().add(entries1k07);
    }

    @Benchmark
    public void defaultTree1kM128Construction() {
        RTree.maxChildren(128).<Object, Rectangle> create().add(entries1k07);
    }


    @Benchmark
    public void starTree1kM4Construction() {
        RTree.maxChildren(4).star().<Object, Rectangle> create().add(entries1k07);
    }

    @Benchmark
    public void starTree1kM10Construction() {
        RTree.maxChildren(10).star().<Object, Rectangle> create().add(entries1k07);
    }

    @Benchmark
    public void starTree1kM32Construction() {
        RTree.maxChildren(32).star().<Object, Rectangle> create().add(entries1k07);
    }

    @Benchmark
    public void starTree1kM128Construction() {
        RTree.maxChildren(128).star().<Object, Rectangle> create().add(entries1k07);
    }


    @Benchmark
    public void strTree1kM4Construction() {
        RTree.maxChildren(4).<Object, Rectangle> create(entries1k07);
    }

    @Benchmark
    public void strTree1kM10Construction() {
        RTree.maxChildren(10).<Object, Rectangle> create(entries1k07);
    }

    @Benchmark
    public void strTree1kM32Construction() {
        RTree.maxChildren(32).<Object, Rectangle> create(entries1k07);
    }

    @Benchmark
    public void strTree1kM128Construction() {
        RTree.maxChildren(128).<Object, Rectangle> create(entries1k07);
    }

    @Benchmark
    public void fbsTree1kM4Construction() {
        createFBSTree(byteArray1kM4);
    }

    @Benchmark
    public void fbsTree1kM10Construction() {
        createFBSTree(byteArray1kM10);
    }

    @Benchmark
    public void fbsTree1kM32Construction() {
        createFBSTree(byteArray1kM32);
    }

    @Benchmark
    public void fbsTree1kM128Construction() {
        createFBSTree(byteArray1kM128);
    }

    // benchmark insert one entry

    @Benchmark
    public void defaultTreeGreekM4InsertOne() {
        defaultTreeGreekM4.add(entriesGreek03.get(0));
    }

    @Benchmark
    public void defaultTreeGreekM10InsertOne() {
        defaultTreeGreekM10.add(entriesGreek03.get(0));
    }

    @Benchmark
    public void defaultTreeGreekM32InsertOne() {
        defaultTreeGreekM32.add(entriesGreek03.get(0));
    }

    @Benchmark
    public void defaultTreeGreekM128InsertOne() {
        defaultTreeGreekM128.add(entriesGreek03.get(0));
    }


    @Benchmark
    public void starTreeGreekM4InsertOne() {
        starTreeGreekM4.add(entriesGreek03.get(0));
    }

    @Benchmark
    public void starTreeGreekM10InsertOne() {
        starTreeGreekM10.add(entriesGreek03.get(0));
    }

    @Benchmark
    public void starTreeGreekM32InsertOne() {
        starTreeGreekM32.add(entriesGreek03.get(0));
    }

    @Benchmark
    public void starTreeGreekM128InsertOne() {
        starTreeGreekM128.add(entriesGreek03.get(0));
    }


    @Benchmark
    public void strTreeGreekM4InsertOne() {
        strTreeGreekM4.add(entriesGreek03.get(0));
    }

    @Benchmark
    public void strTreeGreekM10InsertOne() {
        strTreeGreekM10.add(entriesGreek03.get(0));
    }

    @Benchmark
    public void strTreeGreekM32InsertOne() {
        strTreeGreekM32.add(entriesGreek03.get(0));
    }

    @Benchmark
    public void strTreeGreekM128InsertOne() {
        strTreeGreekM128.add(entriesGreek03.get(0));
    }


    @Benchmark
    public void fbsTreeGreekM4InsertOne() {
        fbsTreeGreekM4.add(entriesGreek03.get(0));
    }

    @Benchmark
    public void fbsTreeGreekM10InsertOne() {
        fbsTreeGreekM10.add(entriesGreek03.get(0));
    }

    @Benchmark
    public void fbsTreeGreekM32InsertOne() {
        fbsTreeGreekM32.add(entriesGreek03.get(0));
    }

    @Benchmark
    public void fbsTreeGreekM128InsertOne() {
        fbsTreeGreekM128.add(entriesGreek03.get(0));
    }


    @Benchmark
    public void defaultTree1kM4InsertOne() {
        defaultTree1kM4.add(entries1k03.get(0));
    }

    @Benchmark
    public void defaultTree1kM10InsertOne() {
        defaultTree1kM10.add(entries1k03.get(0));
    }

    @Benchmark
    public void defaultTree1kM32InsertOne() {
        defaultTree1kM32.add(entries1k03.get(0));
    }

    @Benchmark
    public void defaultTree1kM128InsertOne() {
        defaultTree1kM128.add(entries1k03.get(0));
    }


    @Benchmark
    public void starTree1kM4InsertOne() {
        starTree1kM4.add(entries1k03.get(0));
    }

    @Benchmark
    public void starTree1kM10InsertOne() {
        starTree1kM10.add(entries1k03.get(0));
    }

    @Benchmark
    public void starTree1kM32InsertOne() {
        starTree1kM32.add(entries1k03.get(0));
    }

    @Benchmark
    public void starTree1kM128InsertOne() {
        starTree1kM128.add(entries1k03.get(0));
    }


    @Benchmark
    public void strTree1kM4InsertOne() {
        strTree1kM4.add(entries1k03.get(0));
    }

    @Benchmark
    public void strTree1kM10InsertOne() {
        strTree1kM10.add(entries1k03.get(0));
    }

    @Benchmark
    public void strTree1kM32InsertOne() {
        strTree1kM32.add(entries1k03.get(0));
    }

    @Benchmark
    public void strTree1kM128InsertOne() {
        strTree1kM128.add(entries1k03.get(0));
    }


    @Benchmark
    public void fbsTree1kM4InsertOne() {
        fbsTree1kM4.add(entries1k03.get(0));
    }

    @Benchmark
    public void fbsTree1kM10InsertOne() {
        fbsTree1kM10.add(entries1k03.get(0));
    }

    @Benchmark
    public void fbsTree1kM32InsertOne() {
        fbsTree1kM32.add(entries1k03.get(0));
    }

    @Benchmark
    public void fbsTree1kM128InsertOne() {
        fbsTree1kM128.add(entries1k03.get(0));
    }


    // benchmark delete one entry

    @Benchmark
    public void defaultTreeGreekM4DeleteOne() {
        defaultTreeGreekM4.delete(entriesGreek07.get(0));
    }

    @Benchmark
    public void defaultTreeGreekM10DeleteOne() {
        defaultTreeGreekM10.delete(entriesGreek07.get(0));
    }

    @Benchmark
    public void defaultTreeGreekM32DeleteOne() {
        defaultTreeGreekM32.delete(entriesGreek07.get(0));
    }

    @Benchmark
    public void defaultTreeGreekM128DeleteOne() {
        defaultTreeGreekM128.delete(entriesGreek07.get(0));
    }


    @Benchmark
    public void starTreeGreekM4DeleteOne() {
        starTreeGreekM4.delete(entriesGreek07.get(0));
    }

    @Benchmark
    public void starTreeGreekM10DeleteOne() {
        starTreeGreekM10.delete(entriesGreek07.get(0));
    }

    @Benchmark
    public void starTreeGreekM32DeleteOne() {
        starTreeGreekM32.delete(entriesGreek07.get(0));
    }

    @Benchmark
    public void starTreeGreekM128DeleteOne() {
        starTreeGreekM128.delete(entriesGreek07.get(0));
    }


    @Benchmark
    public void strTreeGreekM4DeleteOne() {
        strTreeGreekM4.delete(entriesGreek07.get(0));
    }

    @Benchmark
    public void strTreeGreekM10DeleteOne() {
        strTreeGreekM10.delete(entriesGreek07.get(0));
    }

    @Benchmark
    public void strTreeGreekM32DeleteOne() {
        strTreeGreekM32.delete(entriesGreek07.get(0));
    }

    @Benchmark
    public void strTreeGreekM128DeleteOne() {
        strTreeGreekM128.delete(entriesGreek07.get(0));
    }


    @Benchmark
    public void fbsTreeGreekM4DeleteOne() {
        fbsTreeGreekM4.delete(entriesGreek07.get(0));
    }

    @Benchmark
    public void fbsTreeGreekM10DeleteOne() {
        fbsTreeGreekM10.delete(entriesGreek07.get(0));
    }

    @Benchmark
    public void fbsTreeGreekM32DeleteOne() {
        fbsTreeGreekM32.delete(entriesGreek07.get(0));
    }

    @Benchmark
    public void fbsTreeGreekM128DeleteOne() {
        fbsTreeGreekM128.delete(entriesGreek07.get(0));
    }


    @Benchmark
    public void defaultTree1kM4DeleteOne() {
        defaultTree1kM4.delete(entries1k07.get(0));
    }

    @Benchmark
    public void defaultTree1kM10DeleteOne() {
        defaultTree1kM10.delete(entries1k07.get(0));
    }

    @Benchmark
    public void defaultTree1kM32DeleteOne() {
        defaultTree1kM32.delete(entries1k07.get(0));
    }

    @Benchmark
    public void defaultTree1kM128DeleteOne() {
        defaultTree1kM128.delete(entries1k07.get(0));
    }


    @Benchmark
    public void starTree1kM4DeleteOne() {
        starTree1kM4.delete(entries1k07.get(0));
    }

    @Benchmark
    public void starTree1kM10DeleteOne() {
        starTree1kM10.delete(entries1k07.get(0));
    }

    @Benchmark
    public void starTree1kM32DeleteOne() {
        starTree1kM32.delete(entries1k07.get(0));
    }

    @Benchmark
    public void starTree1kM128DeleteOne() {
        starTree1kM128.delete(entries1k07.get(0));
    }


    @Benchmark
    public void strTree1kM4DeleteOne() {
        strTree1kM4.delete(entries1k07.get(0));
    }

    @Benchmark
    public void strTree1kM10DeleteOne() {
        strTree1kM10.delete(entries1k07.get(0));
    }

    @Benchmark
    public void strTree1kM32DeleteOne() {
        strTree1kM32.delete(entries1k07.get(0));
    }

    @Benchmark
    public void strTree1kM128DeleteOne() {
        strTree1kM128.delete(entries1k07.get(0));
    }


    @Benchmark
    public void fbsTree1kM4DeleteOne() {
        fbsTree1kM4.delete(entries1k07.get(0));
    }

    @Benchmark
    public void fbsTree1kM10DeleteOne() {
        fbsTree1kM10.delete(entries1k07.get(0));
    }

    @Benchmark
    public void fbsTree1kM32DeleteOne() {
        fbsTree1kM32.delete(entries1k07.get(0));
    }

    @Benchmark
    public void fbsTree1kM128DeleteOne() {
        fbsTree1kM128.delete(entries1k07.get(0));
    }

    // benchmark batch insertion (30% data)

    @Benchmark
    public void defaultTreeGreekM4InsertBatch() {
        RTree<Object, Point> tree = defaultTreeGreekM4;
        for (Entry<Object, Point> entry: entriesGreek03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void defaultTreeGreekM10InsertBatch() {
        RTree<Object, Point> tree = defaultTreeGreekM10;
        for (Entry<Object, Point> entry: entriesGreek03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void defaultTreeGreekM32InsertBatch() {
        RTree<Object, Point> tree = defaultTreeGreekM32;
        for (Entry<Object, Point> entry: entriesGreek03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void defaultTreeGreekM128InsertBatch() {
        RTree<Object, Point> tree = defaultTreeGreekM128;
        for (Entry<Object, Point> entry: entriesGreek03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void starTreeGreekM4InsertBatch() {
        RTree<Object, Point> tree = starTreeGreekM4;
        for (Entry<Object, Point> entry: entriesGreek03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void starTreeGreekM10InsertBatch() {
        RTree<Object, Point> tree = starTreeGreekM10;
        for (Entry<Object, Point> entry: entriesGreek03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void starTreeGreekM32InsertBatch() {
        RTree<Object, Point> tree = starTreeGreekM32;
        for (Entry<Object, Point> entry: entriesGreek03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void starTreeGreekM128InsertBatch() {
        RTree<Object, Point> tree = starTreeGreekM128;
        for (Entry<Object, Point> entry: entriesGreek03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void strTreeGreekM4InsertBatch() {
        RTree<Object, Point> tree = strTreeGreekM4;
        for (Entry<Object, Point> entry: entriesGreek03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void strTreeGreekM10InsertBatch() {
        RTree<Object, Point> tree = strTreeGreekM10;
        for (Entry<Object, Point> entry: entriesGreek03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void strTreeGreekM32InsertBatch() {
        RTree<Object, Point> tree = strTreeGreekM32;
        for (Entry<Object, Point> entry: entriesGreek03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void strTreeGreekM128InsertBatch() {
        RTree<Object, Point> tree = strTreeGreekM128;
        for (Entry<Object, Point> entry: entriesGreek03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void fbsTreeGreekM4InsertBatch() {
        RTree<Object, Point> tree = fbsTreeGreekM4;
        for (Entry<Object, Point> entry: entriesGreek03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void fbsTreeGreekM10InsertBatch() {
        RTree<Object, Point> tree = fbsTreeGreekM10;
        for (Entry<Object, Point> entry: entriesGreek03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void fbsTreeGreekM32InsertBatch() {
        RTree<Object, Point> tree = fbsTreeGreekM32;
        for (Entry<Object, Point> entry: entriesGreek03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void fbsTreeGreekM128InsertBatch() {
        RTree<Object, Point> tree = fbsTreeGreekM128;
        for (Entry<Object, Point> entry: entriesGreek03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void defaultTree1kM4InsertBatch() {
        RTree<Object, Rectangle> tree = defaultTree1kM4;
        for (Entry<Object, Rectangle> entry: entries1k03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void defaultTree1kM10InsertBatch() {
        RTree<Object, Rectangle> tree = defaultTree1kM10;
        for (Entry<Object, Rectangle> entry: entries1k03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void defaultTree1kM32InsertBatch() {
        RTree<Object, Rectangle> tree = defaultTree1kM32;
        for (Entry<Object, Rectangle> entry: entries1k03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void defaultTree1kM128InsertBatch() {
        RTree<Object, Rectangle> tree = defaultTree1kM128;
        for (Entry<Object, Rectangle> entry: entries1k03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void starTree1kM4InsertBatch() {
        RTree<Object, Rectangle> tree = starTree1kM4;
        for (Entry<Object, Rectangle> entry: entries1k03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void starTree1kM10InsertBatch() {
        RTree<Object, Rectangle> tree = starTree1kM10;
        for (Entry<Object, Rectangle> entry: entries1k03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void starTree1kM32InsertBatch() {
        RTree<Object, Rectangle> tree = starTree1kM32;
        for (Entry<Object, Rectangle> entry: entries1k03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void starTree1kM128InsertBatch() {
        RTree<Object, Rectangle> tree = starTree1kM128;
        for (Entry<Object, Rectangle> entry: entries1k03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void strTree1kM4InsertBatch() {
        RTree<Object, Rectangle> tree = strTree1kM4;
        for (Entry<Object, Rectangle> entry: entries1k03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void strTree1kM10InsertBatch() {
        RTree<Object, Rectangle> tree = strTree1kM10;
        for (Entry<Object, Rectangle> entry: entries1k03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void strTree1kM32InsertBatch() {
        RTree<Object, Rectangle> tree = strTree1kM32;
        for (Entry<Object, Rectangle> entry: entries1k03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void strTree1kM128InsertBatch() {
        RTree<Object, Rectangle> tree = strTree1kM128;
        for (Entry<Object, Rectangle> entry: entries1k03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void fbsTree1kM4InsertBatch() {
        RTree<Object, Rectangle> tree = fbsTree1kM4;
        for (Entry<Object, Rectangle> entry: entries1k03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void fbsTree1kM10InsertBatch() {
        RTree<Object, Rectangle> tree = fbsTree1kM10;
        for (Entry<Object, Rectangle> entry: entries1k03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void fbsTree1kM32InsertBatch() {
        RTree<Object, Rectangle> tree = fbsTree1kM32;
        for (Entry<Object, Rectangle> entry: entries1k03)
            tree = tree.add(entry);
    }

    @Benchmark
    public void fbsTree1kM128InsertBatch() {
        RTree<Object, Rectangle> tree = fbsTree1kM128;
        for (Entry<Object, Rectangle> entry: entries1k03)
            tree = tree.add(entry);
    }

    // benchmark batch deletion (all 70% data)

    @Benchmark
    public void defaultTreeGreekM4DeleteBatch() {
        RTree<Object, Point> tree = defaultTreeGreekM4;
        for (Entry<Object, Point> entry: entriesGreek07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void defaultTreeGreekM10DeleteBatch() {
        RTree<Object, Point> tree = defaultTreeGreekM10;
        for (Entry<Object, Point> entry: entriesGreek07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void defaultTreeGreekM32DeleteBatch() {
        RTree<Object, Point> tree = defaultTreeGreekM32;
        for (Entry<Object, Point> entry: entriesGreek07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void defaultTreeGreekM128DeleteBatch() {
        RTree<Object, Point> tree = defaultTreeGreekM128;
        for (Entry<Object, Point> entry: entriesGreek07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void starTreeGreekM4DeleteBatch() {
        RTree<Object, Point> tree = starTreeGreekM4;
        for (Entry<Object, Point> entry: entriesGreek07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void starTreeGreekM10DeleteBatch() {
        RTree<Object, Point> tree = starTreeGreekM10;
        for (Entry<Object, Point> entry: entriesGreek07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void starTreeGreekM32DeleteBatch() {
        RTree<Object, Point> tree = starTreeGreekM32;
        for (Entry<Object, Point> entry: entriesGreek07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void starTreeGreekM128DeleteBatch() {
        RTree<Object, Point> tree = starTreeGreekM128;
        for (Entry<Object, Point> entry: entriesGreek07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void strTreeGreekM4DeleteBatch() {
        RTree<Object, Point> tree = strTreeGreekM4;
        for (Entry<Object, Point> entry: entriesGreek07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void strTreeGreekM10DeleteBatch() {
        RTree<Object, Point> tree = strTreeGreekM10;
        for (Entry<Object, Point> entry: entriesGreek07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void strTreeGreekM32DeleteBatch() {
        RTree<Object, Point> tree = strTreeGreekM32;
        for (Entry<Object, Point> entry: entriesGreek07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void strTreeGreekM128DeleteBatch() {
        RTree<Object, Point> tree = strTreeGreekM128;
        for (Entry<Object, Point> entry: entriesGreek07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void fbsTreeGreekM4DeleteBatch() {
        RTree<Object, Point> tree = fbsTreeGreekM4;
        for (Entry<Object, Point> entry: entriesGreek07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void fbsTreeGreekM10DeleteBatch() {
        RTree<Object, Point> tree = fbsTreeGreekM10;
        for (Entry<Object, Point> entry: entriesGreek07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void fbsTreeGreekM32DeleteBatch() {
        RTree<Object, Point> tree = fbsTreeGreekM32;
        for (Entry<Object, Point> entry: entriesGreek07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void fbsTreeGreekM128DeleteBatch() {
        RTree<Object, Point> tree = fbsTreeGreekM128;
        for (Entry<Object, Point> entry: entriesGreek07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void defaultTree1kM4DeleteBatch() {
        RTree<Object, Rectangle> tree = defaultTree1kM4;
        for (Entry<Object, Rectangle> entry: entries1k07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void defaultTree1kM10DeleteBatch() {
        RTree<Object, Rectangle> tree = defaultTree1kM10;
        for (Entry<Object, Rectangle> entry: entries1k07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void defaultTree1kM32DeleteBatch() {
        RTree<Object, Rectangle> tree = defaultTree1kM32;
        for (Entry<Object, Rectangle> entry: entries1k07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void defaultTree1kM128DeleteBatch() {
        RTree<Object, Rectangle> tree = defaultTree1kM128;
        for (Entry<Object, Rectangle> entry: entries1k07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void starTree1kM4DeleteBatch() {
        RTree<Object, Rectangle> tree = starTree1kM4;
        for (Entry<Object, Rectangle> entry: entries1k07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void starTree1kM10DeleteBatch() {
        RTree<Object, Rectangle> tree = starTree1kM10;
        for (Entry<Object, Rectangle> entry: entries1k07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void starTree1kM32DeleteBatch() {
        RTree<Object, Rectangle> tree = starTree1kM32;
        for (Entry<Object, Rectangle> entry: entries1k07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void starTree1kM128DeleteBatch() {
        RTree<Object, Rectangle> tree = starTree1kM128;
        for (Entry<Object, Rectangle> entry: entries1k07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void strTree1kM4DeleteBatch() {
        RTree<Object, Rectangle> tree = strTree1kM4;
        for (Entry<Object, Rectangle> entry: entries1k07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void strTree1kM10DeleteBatch() {
        RTree<Object, Rectangle> tree = strTree1kM10;
        for (Entry<Object, Rectangle> entry: entries1k07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void strTree1kM32DeleteBatch() {
        RTree<Object, Rectangle> tree = strTree1kM32;
        for (Entry<Object, Rectangle> entry: entries1k07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void strTree1kM128DeleteBatch() {
        RTree<Object, Rectangle> tree = strTree1kM128;
        for (Entry<Object, Rectangle> entry: entries1k07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void fbsTree1kM4DeleteBatch() {
        RTree<Object, Rectangle> tree = fbsTree1kM4;
        for (Entry<Object, Rectangle> entry: entries1k07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void fbsTree1kM10DeleteBatch() {
        RTree<Object, Rectangle> tree = fbsTree1kM10;
        for (Entry<Object, Rectangle> entry: entries1k07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void fbsTree1kM32DeleteBatch() {
        RTree<Object, Rectangle> tree = fbsTree1kM32;
        for (Entry<Object, Rectangle> entry: entries1k07)
            tree = tree.delete(entry);
    }

    @Benchmark
    public void fbsTree1kM128DeleteBatch() {
        RTree<Object, Rectangle> tree = fbsTree1kM128;
        for (Entry<Object, Rectangle> entry: entries1k07)
            tree = tree.delete(entry);
    }

    // benchmark search one

    @Benchmark
    public void defaultTreeGreekM4SearchOne() {
        searchGreek(defaultTreeGreekM4);
    }

    @Benchmark
    public void defaultTreeGreekM10SearchOne() {
        searchGreek(defaultTreeGreekM10);
    }

    @Benchmark
    public void defaultTreeGreekM32SearchOne() {
        searchGreek(defaultTreeGreekM32);
    }

    @Benchmark
    public void defaultTreeGreekM128SearchOne() {
        searchGreek(defaultTreeGreekM128);
    }

    @Benchmark
    public void starTreeGreekM4SearchOne() {
        searchGreek(starTreeGreekM4);
    }

    @Benchmark
    public void starTreeGreekM10SearchOne() {
        searchGreek(starTreeGreekM10);
    }

    @Benchmark
    public void starTreeGreekM32SearchOne() {
        searchGreek(starTreeGreekM32);
    }

    @Benchmark
    public void starTreeGreekM128SearchOne() {
        searchGreek(starTreeGreekM128);
    }

    @Benchmark
    public void strTreeGreekM4SearchOne() {
        searchGreek(strTreeGreekM4);
    }

    @Benchmark
    public void strTreeGreekM10SearchOne() {
        searchGreek(strTreeGreekM10);
    }

    @Benchmark
    public void strTreeGreekM32SearchOne() {
        searchGreek(strTreeGreekM32);
    }

    @Benchmark
    public void strTreeGreekM128SearchOne() {
        searchGreek(strTreeGreekM128);
    }

    @Benchmark
    public void fbsTreeGreekM4SearchOne() {
        searchGreek(fbsTreeGreekM4);
    }

    @Benchmark
    public void fbsTreeGreekM10SearchOne() {
        searchGreek(fbsTreeGreekM10);
    }

    @Benchmark
    public void fbsTreeGreekM32SearchOne() {
        searchGreek(fbsTreeGreekM32);
    }

    @Benchmark
    public void fbsTreeGreekM128SearchOne() {
        searchGreek(fbsTreeGreekM128);
    }

    @Benchmark
    public void defaultTree1kM4SearchOne() {
        search(defaultTree1kM4);
    }

    @Benchmark
    public void defaultTree1kM10SearchOne() {
        search(defaultTree1kM10);
    }

    @Benchmark
    public void defaultTree1kM32SearchOne() {
        search(defaultTree1kM32);
    }

    @Benchmark
    public void defaultTree1kM128SearchOne() {
        search(defaultTree1kM128);
    }

    @Benchmark
    public void starTree1kM4SearchOne() {
        search(starTree1kM4);
    }

    @Benchmark
    public void starTree1kM10SearchOne() {
        search(starTree1kM10);
    }

    @Benchmark
    public void starTree1kM32SearchOne() {
        search(starTree1kM32);
    }

    @Benchmark
    public void starTree1kM128SearchOne() {
        search(starTree1kM128);
    }

    @Benchmark
    public void strTree1kM4SearchOne() {
        search(strTree1kM4);
    }

    @Benchmark
    public void strTree1kM10SearchOne() {
        search(strTree1kM10);
    }

    @Benchmark
    public void strTree1kM32SearchOne() {
        search(strTree1kM32);
    }

    @Benchmark
    public void strTree1kM128SearchOne() {
        search(strTree1kM128);
    }

    @Benchmark
    public void fbsTree1kM4SearchOne() {
        search(fbsTree1kM4);
    }

    @Benchmark
    public void fbsTree1kM10SearchOne() {
        search(fbsTree1kM10);
    }

    @Benchmark
    public void fbsTree1kM32SearchOne() {
        search(fbsTree1kM32);
    }

    @Benchmark
    public void fbsTree1kM128SearchOne() {
        search(fbsTree1kM128);
    }

    // benchmark search nearest neighbor

    @Benchmark
    public void defaultTreeGreekM4SearchNearestOne() {
        searchNearestGreek(defaultTreeGreekM4);
    }

    @Benchmark
    public void defaultTreeGreekM10SearchNearestOne() {
        searchNearestGreek(defaultTreeGreekM10);
    }

    @Benchmark
    public void defaultTreeGreekM32SearchNearestOne() {
        searchNearestGreek(defaultTreeGreekM32);
    }

    @Benchmark
    public void defaultTreeGreekM128SearchNearestOne() {
        searchNearestGreek(defaultTreeGreekM128);
    }

    @Benchmark
    public void starTreeGreekM4SearchNearestOne() {
        searchNearestGreek(starTreeGreekM4);
    }

    @Benchmark
    public void starTreeGreekM10SearchNearestOne() {
        searchNearestGreek(starTreeGreekM10);
    }

    @Benchmark
    public void starTreeGreekM32SearchNearestOne() {
        searchNearestGreek(starTreeGreekM32);
    }

    @Benchmark
    public void starTreeGreekM128SearchNearestOne() {
        searchNearestGreek(starTreeGreekM128);
    }

    @Benchmark
    public void strTreeGreekM4SearchNearestOne() {
        searchNearestGreek(strTreeGreekM4);
    }

    @Benchmark
    public void strTreeGreekM10SearchNearestOne() {
        searchNearestGreek(strTreeGreekM10);
    }

    @Benchmark
    public void strTreeGreekM32SearchNearestOne() {
        searchNearestGreek(strTreeGreekM32);
    }

    @Benchmark
    public void strTreeGreekM128SearchNearestOne() {
        searchNearestGreek(strTreeGreekM128);
    }

    @Benchmark
    public void fbsTreeGreekM4SearchNearestOne() {
        searchNearestGreek(fbsTreeGreekM4);
    }

    @Benchmark
    public void fbsTreeGreekM10SearchNearestOne() {
        searchNearestGreek(fbsTreeGreekM10);
    }

    @Benchmark
    public void fbsTreeGreekM32SearchNearestOne() {
        searchNearestGreek(fbsTreeGreekM32);
    }

    @Benchmark
    public void fbsTreeGreekM128SearchNearestOne() {
        searchNearestGreek(fbsTreeGreekM128);
    }

    @Benchmark
    public void defaultTree1kM4SearchNearestOne() {
        searchNearest(defaultTree1kM4);
    }

    @Benchmark
    public void defaultTree1kM10SearchNearestOne() {
        searchNearest(defaultTree1kM10);
    }

    @Benchmark
    public void defaultTree1kM32SearchNearestOne() {
        searchNearest(defaultTree1kM32);
    }

    @Benchmark
    public void defaultTree1kM128SearchNearestOne() {
        searchNearest(defaultTree1kM128);
    }

    @Benchmark
    public void starTree1kM4SearchNearestOne() {
        searchNearest(starTree1kM4);
    }

    @Benchmark
    public void starTree1kM10SearchNearestOne() {
        searchNearest(starTree1kM10);
    }

    @Benchmark
    public void starTree1kM32SearchNearestOne() {
        searchNearest(starTree1kM32);
    }

    @Benchmark
    public void starTree1kM128SearchNearestOne() {
        searchNearest(starTree1kM128);
    }

    @Benchmark
    public void strTree1kM4SearchNearestOne() {
        searchNearest(strTree1kM4);
    }

    @Benchmark
    public void strTree1kM10SearchNearestOne() {
        searchNearest(strTree1kM10);
    }

    @Benchmark
    public void strTree1kM32SearchNearestOne() {
        searchNearest(strTree1kM32);
    }

    @Benchmark
    public void strTree1kM128SearchNearestOne() {
        searchNearest(strTree1kM128);
    }

    @Benchmark
    public void fbsTree1kM4SearchNearestOne() {
        searchNearest(fbsTree1kM4);
    }

    @Benchmark
    public void fbsTree1kM10SearchNearestOne() {
        searchNearest(fbsTree1kM10);
    }

    @Benchmark
    public void fbsTree1kM32SearchNearestOne() {
        searchNearest(fbsTree1kM32);
    }

    @Benchmark
    public void fbsTree1kM128SearchNearestOne() {
        searchNearest(fbsTree1kM128);
    }

    private void search(RTree<Object, Rectangle> tree) {
        // returns 10 results
        tree.search(Geometries.rectangle(500, 500, 630, 630)).subscribe();
    }

    private void searchGreek(RTree<Object, Point> tree) {
        // should return 22 results
        tree.search(Geometries.rectangle(40, 27.0, 40.5, 27.5)).subscribe();
    }

    private void searchGreekBackpressure(RTree<Object, Point> tree) {
        // should return 22 results
        tree.search(Geometries.rectangle(40, 27.0, 40.5, 27.5)).take(1000).subscribe();
    }

    private void searchNearestGreek(RTree<Object, Point> tree) {
        tree.nearest(Geometries.point(40.0, 27.0), 1, 300).subscribe();
    }

    private void searchNearest(RTree<Object, Rectangle> tree) {
        tree.nearest(Geometries.point(500.0, 500.0), 10, 30).subscribe();
    }

    private void searchGreekWithBackpressure(RTree<Object, Point> tree) {
        // should return 22 results
        tree.search(Geometries.rectangle(40, 27.0, 40.5, 27.5)).subscribe(new Subscriber<Object>() {

            @Override
            public void onStart() {
                request(1);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable arg0) {

            }

            @Override
            public void onNext(Object arg0) {
                request(1);
            }
        });
    }

    private void insertRectangle(RTree<Object, Rectangle> tree) {
        tree.add(new Object(), RTreeTest.random());
    }

    private void insertPoint(RTree<Object, Point> tree) {
        tree.add(new Object(), Geometries.point(Math.random() * 1000, Math.random() * 1000));
    }

    public static void main(String[] args) {
        BenchmarksRTree b = new BenchmarksRTree();
        System.out.println("starting searches");
        while (true)
            b.fbsTreeGreekM4SearchOne();
    }
}
